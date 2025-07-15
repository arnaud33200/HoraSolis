package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import kotlinx.collections.immutable.toImmutableList
import java.time.format.DateTimeFormatter

class AlarmListModelFactory {

    fun create(
        savedAlarms: List<SavedAlarm>,
        solisDay: SolisDay?,
    ): AlarmListModel {
        return AlarmListModel(
            items = savedAlarms.map {
                it.toAlarmItemModel(solisDay)
            }.toImmutableList(),
        )
    }

    private fun SavedAlarm.toAlarmItemModel(solisDay: SolisDay?): AlarmItemModel {
        val formatted = solisTime.format()
        val civilTime = solisDay?.let {
            DateTimeFormatter.ofPattern("HH:mm").format(solisTime.toCivilTime(it))
        }
        return AlarmItemModel(
            id = id,
            title = formatted,
            civilTime = civilTime ?: "--:--",
        )
    }

    fun SolisTime.format(): String {
        val emoji = when (type) {
            SolisTime.Type.Day -> "\u2600\uFE0F"
            SolisTime.Type.Night -> "\uD83C\uDF1A"
        }
        return "%02d %s %02d".format(hour, emoji, minute)
    }

}