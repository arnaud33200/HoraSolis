package ca.arnaud.horasolis.ui.common

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.provider.LocaleProvider
import io.ktor.util.date.WeekDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle

class DateFormatter(
    private val localeProvider: LocaleProvider,
    private val stringProvider: StringProvider,
) {

    fun formatCivilTime(time: LocalTime): String {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(localeProvider.getLocale())
            .format(time)
    }

    fun formatDate(date: LocalDate): String {
        val pattern = stringProvider.getString(R.string.alarm_schedule_date_pattern)
        return DateTimeFormatter.ofPattern(pattern, localeProvider.getLocale()).format(date)
    }

    fun formatWeekDay(weekDay: WeekDay): String {
        return weekDay.toJavaDayOfWeek()
            .getDisplayName(TextStyle.SHORT_STANDALONE, localeProvider.getLocale())
    }

    fun formatWeekDaysOrNull(weekDays: Set<WeekDay>): String? {
        val sorted = weekDays.sortedBy { it.ordinal }
        return when {
            sorted.isEmpty() -> null
            sorted.size == WeekDay.entries.size ->
                stringProvider.getString(R.string.alarm_schedule_every_day)

            sorted.toSet() == setOf(WeekDay.SATURDAY, WeekDay.SUNDAY) ->
                stringProvider.getString(R.string.alarm_schedule_weekend)

            sorted.size == 1 -> sorted.firstOrNull()?.let { formatWeekDay(it) }
            isConsecutive(sorted) -> {
                val first = sorted.firstOrNull() ?: return null
                val last = sorted.lastOrNull() ?: return null
                stringProvider.getString(
                    R.string.alarm_schedule_range,
                    formatWeekDay(first),
                    formatWeekDay(last),
                )
            }

            else -> sorted.joinToString(", ") { formatWeekDay(it) }
        }
    }

    private fun isConsecutive(sorted: List<WeekDay>): Boolean =
        sorted.zipWithNext().all { (a, b) -> b.ordinal == a.ordinal + 1 }
}

private fun WeekDay.toJavaDayOfWeek(): DayOfWeek = when (this) {
    WeekDay.MONDAY -> DayOfWeek.MONDAY
    WeekDay.TUESDAY -> DayOfWeek.TUESDAY
    WeekDay.WEDNESDAY -> DayOfWeek.WEDNESDAY
    WeekDay.THURSDAY -> DayOfWeek.THURSDAY
    WeekDay.FRIDAY -> DayOfWeek.FRIDAY
    WeekDay.SATURDAY -> DayOfWeek.SATURDAY
    WeekDay.SUNDAY -> DayOfWeek.SUNDAY
}
