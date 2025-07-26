package ca.arnaud.horasolis.domain.model

import java.time.Duration
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

fun LocalTime.toSolisTime(solisDay: SolisDay): SolisTime {
    val isDay = this.isAfter(solisDay.civilSunriseTime)
            && !this.isAfter(solisDay.civilSunsetTime)
    val startTime = if (isDay) solisDay.civilSunriseTime else solisDay.civilSunsetTime
    val durationSinceStart = Duration.between(startTime, this)
    val totalMinutes = durationSinceStart.toMinutes().toInt()
    val hour = (totalMinutes / 60) + 1
    val minute = totalMinutes % 60
    return SolisTime(
        hour = hour.coerceIn(1, 12),
        minute = minute,
        type = if (isDay) SolisTime.Type.Day else SolisTime.Type.Night
    )
}
