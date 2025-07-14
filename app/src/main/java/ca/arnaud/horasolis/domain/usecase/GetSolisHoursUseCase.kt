package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.map
import ca.arnaud.horasolis.domain.model.SolisHour
import ca.arnaud.horasolis.domain.model.SunTime
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SolisTimes(
    val location: UserLocation,
    val date: LocalDate,
    val times: List<SolisHour>,
    val dayDuration: Duration,
    val nightDuration: Duration,
)

/**
 * GetRomanTimesParams(lat=43.6532, lng=-79.3832, timZoneId=America/New_York, date=2025-06-28)
 */
data class GetSolisHoursParams(
    val location: UserLocation,
    val date: LocalDate,
)

class GetSolisHoursUseCase(
    private val ktorClient: KtorClient
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12
        private const val NIGHT_HOUR_COUNT = 12
        private val fullDayDuration = Duration.ofHours(24)
    }

    suspend operator fun invoke(
        params: GetSolisHoursParams,
    ): Response<SolisTimes, Throwable> {
        val resource = GetSunTime(
            lat = params.location.lat,
            lng = params.location.lng,
            tzid = params.location.timZoneId,
            date = params.date.toIsoString(),
        )
        return ktorClient.getResponse<GetSunTime, RemoteSunTimeResponse>(resource).map(
            transform = { remoteSunTimeResponse ->
                val sunTime = remoteSunTimeResponse.toSunTime(params.date)
                calculateRomanTimes(sunTime, params)
            }
        )
    }

    private fun calculateRomanTimes(
        sunTime: SunTime,
        params: GetSolisHoursParams,
    ): SolisTimes {
        val dayDuration = Duration.between(sunTime.sunrise, sunTime.sunset)
        val dayTimes = sunTime.sunrise
            .toRomanDayTimes(sunTime.sunset)
            .toRomanTimes(dayDuration, SolisHour.Type.Day)

        val nightDuration = fullDayDuration.minus(dayDuration)
        val nightTimes = sunTime.sunset
            .toRomanNightTimes(nightDuration)
            .toRomanTimes(nightDuration, SolisHour.Type.Night)

        return SolisTimes(
            date = sunTime.date,
            times = dayTimes + nightTimes,
            location = params.location,
            dayDuration = dayDuration,
            nightDuration = nightDuration,
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
        nightDuration: Duration
    ): List<LocalTime> {
        val unitDuration = nightDuration.dividedBy(NIGHT_HOUR_COUNT.toLong())
        return List(NIGHT_HOUR_COUNT) { index ->
            this.plus(unitDuration.multipliedBy(index.toLong()))
        }
    }

    private fun List<LocalTime>.toRomanTimes(
        duration: Duration,
        type: SolisHour.Type,
    ): List<SolisHour> {
        val count = if (type == SolisHour.Type.Day) DAY_HOUR_COUNT else NIGHT_HOUR_COUNT
        val timeDuration = duration.dividedBy(count.toLong())
        val offsetIndex = if (type == SolisHour.Type.Day) 0 else DAY_HOUR_COUNT
        return this.mapIndexed { index, time ->
            SolisHour(
                number = index + 1 + offsetIndex,
                startTime = time,
                duration = timeDuration,
                type = type,
            )
        }
    }
}