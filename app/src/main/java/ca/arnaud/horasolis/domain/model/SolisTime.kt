package ca.arnaud.horasolis.domain.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Represents a time in the Solis system, which can be either day or night.
 *
 * @param hour The hour of the time (1-12).
 * @param minute The minute of the time (0-59).
 * @param type The type of time, either [Type.Day] or [Type.Night].
 */
data class SolisTime(
    val hour: Int,
    val minute: Int,
    val type: Type,
) {

    enum class Type {
        Day, Night;

        companion object {

            fun fromNameOrNull(name: String): Type? {
                return Type.entries.firstOrNull { it.name == name }
            }
        }
    }

    fun toCivilTime(solisDay: SolisDay): LocalTime {
        val startTime = when (type) {
            Type.Day -> solisDay.civilSunriseTime
            Type.Night -> solisDay.civilSunsetTime
        }
        val oneHourDuration = when (type) {
            Type.Day -> solisDay.solisDayHourDuration
            Type.Night -> solisDay.solisNightHourDuration
        }
        val hourDuration = oneHourDuration.multipliedBy(hour - 1L)

        val oneMinuteDuration = oneHourDuration.dividedBy(60)
        val minuteDuration = oneMinuteDuration.multipliedBy(minute.toLong())

        return startTime + hourDuration + minuteDuration
    }

    fun plusMinutes(minutesToAdd: Int): SolisTime {
        val newMinute = minute + minutesToAdd
        val newHour = if (newMinute >= 60) hour + 1 else hour
        val type = if (newHour > 12) {
            if (type == Type.Day) Type.Night else Type.Day
        } else {
            type
        }

        return SolisTime(
            hour = (newHour % 13).takeIf { it > 0 } ?: 1,
            minute = newMinute % 60,
            type = type
        )
    }
}

fun LocalDateTime.toSolisTime(solisDay: SolisDay): SolisTime {
    val sunriseTime = solisDay.civilSunriseTime
    val sunsetTime = solisDay.civilSunsetTime

    // Compose LocalDateTime for sunrise and sunset on the same day as this
    val date = this.toLocalDate()
    val sunriseDateTime = sunriseTime.atDate(date)
    val sunsetDateTime = sunsetTime.atDate(date)

    val isDay = this >= sunriseDateTime && this < sunsetDateTime

    val startDateTime = if (isDay) sunriseDateTime else {
        if (this < sunriseDateTime) {
            // between midnight and sunrise: sunset of the previous day
            sunsetTime.atDate(date.minusDays(1))
        } else {
            // Night after sunset: sunset of this day
            sunsetDateTime
        }
    }
    val endDateTime = if (isDay) {
        sunsetDateTime
    } else {
        sunriseTime.atDate(startDateTime.toLocalDate().plusDays(1))
    }
    val totalDuration = Duration.between(startDateTime, endDateTime)
    val oneHourDuration = totalDuration.dividedBy(12)
    val durationSinceStart = Duration.between(startDateTime, this)
    val hour = (durationSinceStart.toMillis() / oneHourDuration.toMillis()).toInt() + 1
    val minute = ((durationSinceStart.toMillis() % oneHourDuration.toMillis()) / (oneHourDuration.toMillis() / 60)).toInt()

    return SolisTime(
        hour = hour.coerceIn(1, 12),
        minute = minute.coerceIn(0, 59),
        type = if (isDay) SolisTime.Type.Day else SolisTime.Type.Night
    )
}
