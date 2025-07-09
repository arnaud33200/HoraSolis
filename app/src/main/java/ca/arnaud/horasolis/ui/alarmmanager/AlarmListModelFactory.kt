package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.extension.formatSunTime
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
        val formatted = solisTime.formatSunTime()
        return AlarmItemModel(
            id = id,
            title = formatted,
        )
    }
}