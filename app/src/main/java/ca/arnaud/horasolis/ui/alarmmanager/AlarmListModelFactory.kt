package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.extension.format
import ca.arnaud.horasolis.ui.DayOfWeekItemModel
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.toImmutableList
import java.time.format.DateTimeFormatter

class AlarmListModelFactory {

    fun create(
        savedAlarms: List<SavedAlarm>,
        solisDay: SolisDay?,
    ): AlarmListModel {
        val sortedAlarms = savedAlarms.sortedWith { alarm1, alarm2 ->
            alarm1.compareTo(alarm2)
        }

        return AlarmListModel(
            items = sortedAlarms.map {
                it.toAlarmItemModel(solisDay)
            }.toImmutableList(),
        )
    }

    private fun SavedAlarm.toAlarmItemModel(solisDay: SolisDay?): AlarmItemModel {
        val formatted = solisTime.format()
        val civilTime = solisDay?.let {
            DateTimeFormatter.ofPattern("HH:mm").format(solisTime.toCivilTime(it))
        }
        val dayOfWeeks = WeekDay.entries.map { dayOfWeek ->
            DayOfWeekItemModel(
                text = dayOfWeek.name.substring(0, 3), // TODO - needs to be localized
                selected = onForWeekDays.contains(dayOfWeek)
            )
        }
        return AlarmItemModel(
            id = id,
            title = formatted,
            civilTime = civilTime ?: "--:--",
            isEnabled = enabled,
            dayOfWeeks = dayOfWeeks.toImmutableList(),
        )
    }

}