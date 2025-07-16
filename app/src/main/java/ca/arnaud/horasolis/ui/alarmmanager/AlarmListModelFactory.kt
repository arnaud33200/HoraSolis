package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.extension.format
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

}