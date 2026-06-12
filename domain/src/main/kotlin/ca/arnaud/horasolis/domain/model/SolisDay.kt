package ca.arnaud.horasolis.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Sunrise and sunset times to calculate days & night hours.
 *
 * @property atDate The date for which the sunrise and sunset times are calculated.
 *  Sunset/Sunrise time is different depending on the date.
 * @property civilSunriseTime The time of sunrise for the given date and location.
 *  Used to calculate day & night times
 * @property civilSunsetTime The time of sunset for the given date and location.
 *  Used to calculate day & night times
 * @property location The location for which the sunrise and sunset times are calculated.
 *  Used to show current location in the clock
 */
data class SolisDay(
    val atDate: LocalDate,
    val civilSunriseTime: LocalTime,
    val civilSunsetTime: LocalTime,
    val location: SavedLocation,
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12L
        private const val NIGHT_HOUR_COUNT = 12L
        private const val NUMBER_OF_SECONDS_IN_HOUR = 3600L
        private val fullDayDuration = Duration.ofHours(24)

    }

    val dayDuration = Duration.between(civilSunriseTime, civilSunsetTime)
    val solisDayHourDuration = dayDuration.dividedBy(DAY_HOUR_COUNT)
    val solisDaySecondDuration = solisDayHourDuration.dividedBy(NUMBER_OF_SECONDS_IN_HOUR)

    val nightDuration = fullDayDuration.minus(dayDuration)
    val solisNightHourDuration = nightDuration.dividedBy(NIGHT_HOUR_COUNT)
    val solisNightSecondDuration = solisNightHourDuration.dividedBy(NUMBER_OF_SECONDS_IN_HOUR)
}
