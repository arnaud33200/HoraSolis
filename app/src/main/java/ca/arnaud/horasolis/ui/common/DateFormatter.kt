package ca.arnaud.horasolis.ui.common

import ca.arnaud.horasolis.domain.provider.LocaleProvider
import io.ktor.util.date.WeekDay
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle

class DateFormatter(
    private val localeProvider: LocaleProvider,
) {

    fun formatCivilTime(time: LocalTime): String {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(localeProvider.getLocale())
            .format(time)
    }

    fun formatWeekDay(weekDay: WeekDay): String {
        return weekDay.toJavaDayOfWeek()
            .getDisplayName(TextStyle.SHORT_STANDALONE, localeProvider.getLocale())
    }
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
