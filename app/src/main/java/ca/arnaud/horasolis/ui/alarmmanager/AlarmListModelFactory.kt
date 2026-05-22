package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.alarm.Alarm.Schedule
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.extension.format
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.toImmutableList

class AlarmListModelFactory(
    private val dateFormatter: DateFormatter,
    private val stringProvider: StringProvider,
    private val timeProvider: TimeProvider,
) {

    fun create(
        savedAlarms: List<SavedAlarm>,
        solisDay: SolisDay,
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

    private fun SavedAlarm.toAlarmItemModel(solisDay: SolisDay): AlarmItemModel {
        val isExpired = this.isExpired(solisDay, timeProvider.getNowDateTime())
        return AlarmItemModel(
            id = id,
            title = solisTime.format(),
            label = label,
            civilTime = dateFormatter.formatCivilTime(solisTime.toCivilTime(solisDay)),
            isEnabled = enabled.takeIf { !isExpired },
            schedule = when (schedule) {
                is Schedule.Repeating -> dateFormatter.formatWeekDaysOrNull(schedule.weekDays)
                is Schedule.OneTime -> stringProvider.getString(
                    R.string.alarm_schedule_one_time_date,
                    dateFormatter.formatDate(schedule.date),
                )
            },
        )
    }
}
