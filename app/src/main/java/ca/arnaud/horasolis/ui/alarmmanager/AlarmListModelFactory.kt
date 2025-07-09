package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisTime
import kotlinx.collections.immutable.toImmutableList

class AlarmListModelFactory {

    fun create(
        savedAlarms: List<SavedAlarm>,
    ): AlarmListModel {
        return AlarmListModel(
            items = savedAlarms.map {
                it.toAlarmItemModel()
            }.toImmutableList(),
        )
    }

    private fun SavedAlarm.toAlarmItemModel(): AlarmItemModel {
        val formatted = solisTime.format()
        return AlarmItemModel(
            id = id,
            title = formatted,
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