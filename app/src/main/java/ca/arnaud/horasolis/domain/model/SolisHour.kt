package ca.arnaud.horasolis.domain.model

import java.time.Duration
import java.time.LocalTime

/**
 * Data for one time of the day or night.
 * For one full day, there are 12 day times and 12 night times.
 *
 * TODO
 *  - rename RomanTime to SolisTime
 *  - rename startTime to civilStartTime
 *  - add SolisHour and SolisMinute
 *
 * @param number The number of the time in the day or night (1 to 24).
 * @param startTime The start time of the day or night.
 * @param duration The duration of the time in hours.
 * @param type The type of the time, either Day or Night.
 */
data class SolisHour(
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