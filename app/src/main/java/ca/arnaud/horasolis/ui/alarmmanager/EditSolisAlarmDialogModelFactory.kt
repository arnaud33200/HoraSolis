package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import java.time.format.DateTimeFormatter

class EditSolisAlarmDialogModelFactory {

    fun createNewAlarm(
        solisDay: SolisDay?,
    ): EditSolisAlarmDialogModel {
        return EditSolisAlarmDialogModel(
            toCivilTime = getCivilTimeFormatter(solisDay),
        )
    }

    fun createEditAlarm(
        alarm: SavedAlarm,
        solisDay: SolisDay?,
    ): EditSolisAlarmDialogModel {
        return EditSolisAlarmDialogModel(
            id = alarm.id,
            hour = alarm.solisTime.hour,
            minute = alarm.solisTime.minute,
            isDay = alarm.solisTime.type == SolisTime.Type.Day,
            toCivilTime = getCivilTimeFormatter(solisDay),
        )
    }

    private fun getCivilTimeFormatter(
        solisDay: SolisDay?,
    ): (hour: Int, minute: Int, isDay: Boolean) -> String {
        return { hour, minute, isDay ->
            val solisTime = SolisTime(
                hour = hour,
                minute = minute,
                type = if (isDay) SolisTime.Type.Day else SolisTime.Type.Night,
            )
            solisDay?.let {
                DateTimeFormatter.ofPattern("HH:mm").format(solisTime.toCivilTime(it))
            } ?: ""
        }
    }
}