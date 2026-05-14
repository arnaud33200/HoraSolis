package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.extension.format
import ca.arnaud.horasolis.ui.common.DateFormatter
import kotlinx.collections.immutable.toImmutableList

class AlarmListModelFactory(
    private val dateFormatter: DateFormatter,
) {

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
        return AlarmItemModel(
            id = id,
            title = solisTime.format(),
            label = label,
            civilTime = solisDay?.let { dateFormatter.formatCivilTime(solisTime.toCivilTime(it)) } ?: "--:--",
            isEnabled = enabled,
            schedule = dateFormatter.formatWeekDaysOrNull(onForWeekDays),
        )
    }
}
