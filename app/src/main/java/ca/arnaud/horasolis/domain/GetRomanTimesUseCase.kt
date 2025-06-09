package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class GetRomanTimesParams(
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
    val date: LocalDate,
)

data class SunTime(
    val date: LocalDate,
    val sunrise: LocalTime,
    val sunset: LocalTime,
)

data class RomanTimes(
    val date: LocalDate,
    val dayTimes: List<LocalTime>,
    val nightTimes: List<LocalTime>,
)

class GetRomanTimesUseCase(
    private val ktorClient: KtorClient
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12
        private const val NIGHT_HOUR_COUNT = 12
        private val fullDayDuration = Duration.ofHours(24)
    }

    suspend operator fun invoke(
        params: GetRomanTimesParams,
    ): Response<RomanTimes, Throwable> {
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

    private fun calculateRomanTimes(sunTime: SunTime): RomanTimes {
        val dayDuration = Duration.between(sunTime.sunrise, sunTime.sunset)
        val dayTimes = sunTime.sunrise.toRomanDayTimes(sunTime.sunset)
        val nightTimes = sunTime.sunset.toRomanNightTimes(dayDuration)
        return RomanTimes(
            date = sunTime.date,
            dayTimes = dayTimes,
            nightTimes = nightTimes,
        )
    }

    private fun LocalTime.toRomanDayTimes(
        sunsetTime: LocalTime
    ): List<LocalTime> {
        val duration = Duration.between(this, sunsetTime)
        val unitDuration = duration.dividedBy(DAY_HOUR_COUNT.toLong())
        return List(DAY_HOUR_COUNT) { index ->
            this.plus(unitDuration.multipliedBy(index.toLong()))
        }
    }

    private fun LocalTime.toRomanNightTimes(
        dayDuration: Duration
    ): List<LocalTime> {
        val nightDuration = fullDayDuration.minus(dayDuration)
        val unitDuration = nightDuration.dividedBy(NIGHT_HOUR_COUNT.toLong())
        return List(NIGHT_HOUR_COUNT) { index ->
            this.plus(unitDuration.multipliedBy(index.toLong()))
        }
    }
}