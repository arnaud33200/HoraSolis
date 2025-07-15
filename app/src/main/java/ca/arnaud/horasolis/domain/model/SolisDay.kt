package ca.arnaud.horasolis.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SolisDay(
    val atDate: LocalDate,
    val civilSunriseTime: LocalTime,
    val civilSunsetTime: LocalTime,
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12L
        private const val NIGHT_HOUR_COUNT = 12L
        private val fullDayDuration = Duration.ofHours(24)
    }

    val dayDuration = Duration.between(civilSunriseTime, civilSunsetTime)
    val solisDayHourDuration = dayDuration.dividedBy(DAY_HOUR_COUNT)

    val nightDuration = fullDayDuration.minus(dayDuration)
    val solisNightHourDuration = nightDuration.dividedBy(NIGHT_HOUR_COUNT)
}
