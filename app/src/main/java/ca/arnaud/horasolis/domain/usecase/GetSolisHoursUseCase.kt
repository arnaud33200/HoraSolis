package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.SolisRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.map
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.toIsoString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SolisCivilTimes(
    val location: UserLocation,
    val date: LocalDate,
    val times: List<SolisCivilTime>,
    val dayDuration: Duration,
    val nightDuration: Duration,
)

/**
 * Data for one time of the day or night.
 * For one full day, there are 12 day times and 12 night times.
 *
 * @param number The number of the time in the day or night (1 to 24).
 * @param startTime The start time of the day or night.
 * @param duration The duration of the time in hours.
 * @param type The type of the time, either Day or Night.
 */
data class SolisCivilTime(
    val number: Int,
    val startTime: LocalTime,
    val duration: Duration,
    val type: Type,
) {

    val endTime: LocalTime = startTime.plus(duration)

    enum class Type {
        Day, Night
    }

    fun isNow(nowTime: LocalTime): Boolean {
        return nowTime.isAfter(startTime) && nowTime.isBefore(endTime)
    }
}

class GetSolisHoursUseCase(
    private val solisRepository: SolisRepository,
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12
        private const val NIGHT_HOUR_COUNT = 12
        private val fullDayDuration = Duration.ofHours(24)
    }

    suspend operator fun invoke(
        params: GetSolisDayParams,
    ): Response<SolisCivilTimes, Throwable> {
        val resource = GetSunTime(
            lat = params.location.lat,
            lng = params.location.lng,
            tzid = params.location.timZoneId,
            date = params.date.toIsoString(),
        )
        return solisRepository.getSolisDay(params).map(
            transform = { solisDay ->
                calculateRomanTimes(solisDay, params)
            }
        )
    }

    private fun calculateRomanTimes(
        solisDay: SolisDay,
        params: GetSolisDayParams,
    ): SolisCivilTimes {
        val dayDuration = Duration.between(
            solisDay.civilSunriseTime,
            solisDay.civilSunsetTime,
        )
        val dayTimes = solisDay.civilSunriseTime
            .toRomanDayTimes(solisDay.civilSunsetTime)
            .toRomanTimes(dayDuration, SolisCivilTime.Type.Day)

        val nightDuration = fullDayDuration.minus(dayDuration)
        val nightTimes = solisDay.civilSunsetTime
            .toRomanNightTimes(nightDuration)
            .toRomanTimes(nightDuration, SolisCivilTime.Type.Night)

        return SolisCivilTimes(
            date = solisDay.atDate,
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
        type: SolisCivilTime.Type,
    ): List<SolisCivilTime> {
        val count = if (type == SolisCivilTime.Type.Day) DAY_HOUR_COUNT else NIGHT_HOUR_COUNT
        val timeDuration = duration.dividedBy(count.toLong())
        val offsetIndex = if (type == SolisCivilTime.Type.Day) 0 else DAY_HOUR_COUNT
        return this.mapIndexed { index, time ->
            SolisCivilTime(
                number = index + 1 + offsetIndex,
                startTime = time,
                duration = timeDuration,
                type = type,
            )
        }
    }
}