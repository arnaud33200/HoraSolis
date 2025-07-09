package ca.arnaud.horasolis.extension

import java.time.LocalTime

fun LocalTime.formatSunTime(): String {
    return formatSunTime(hour, minute)
}

/**
 * Formats the LocalTime to a string with 12-hour format and appropriate emoji.
 *
 * @param hours The hour in 24-hour format, from 0 to 23.
 * @param minutes The minute, from 0 to 59.
 * @return A formatted string representing the time in 12-hour format with an emoji.
 */
fun formatSunTime(hours: Int, minutes: Int): String {
    val isDay = hours in 0..11 // AM is day time
    val emoji = if (isDay) "\u2600\uFE0F" else "\uD83C\uDF1A"
    val hour12 = ((hours - 1) % 12 + 1) // 1-12 format
    return "%02d %s %02d".format(hour12, emoji, minutes)
}
