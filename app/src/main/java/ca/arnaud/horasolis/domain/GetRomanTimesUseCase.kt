package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class GetRomanTimesParams(
    val lat: Double,
    val lng: Double,
    val timZoneId: String = ZoneId.systemDefault().id,
    val date: LocalDate,
)

data class SunTime(
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
)

class GetRomanTimesUseCase(
    private val ktorClient: KtorClient
) {

    companion object {
        private const val DAY_HOUR_COUNT = 12
        private const val NIGHT_HOUR_COUNT = 12
    }

    suspend operator fun invoke(
        params: GetRomanTimesParams,
    ): Response<List<LocalDateTime>, Throwable> {
        val resource = GetSunTime(
            lat = params.lat,
            lng = params.lng,
            tzid = params.timZoneId,
            date = params.date.toIsoString(),
        )
        return ktorClient.getResponse<GetSunTime, RemoteSunTimeResponse>(resource).map(
            transform = { remoteSunTimeResponse ->
                val sunTime = remoteSunTimeResponse.toSunTime(params.date)
                calculateRomanTimes(sunTime)
            }
        )
    }


    private fun calculateRomanTimes(sunTime: SunTime): List<LocalDateTime> {
        val times = mutableListOf<LocalDateTime>()

        val dayDuration = Duration.between(sunTime.sunrise, sunTime.sunset).toMillis()
        val fullDayMillis = 24 * 60 * 60 * 1000L
        val nightDuration = fullDayMillis - dayDuration

        val dayUnitDuration = dayDuration / DAY_HOUR_COUNT
        val nightUnitDuration = nightDuration / NIGHT_HOUR_COUNT

        // Day units
        for (i in 0 until DAY_HOUR_COUNT) {
            val unitStart = sunTime.sunrise.plusNanos((i * dayUnitDuration) * 1_000_000)
            times.add(unitStart)
        }

        // Night units
        for (i in 0 until NIGHT_HOUR_COUNT) {
            val unitStart = sunTime.sunset.plusNanos((i * nightUnitDuration) * 1_000_000)
            times.add(unitStart)
        }

        return times
    }
}