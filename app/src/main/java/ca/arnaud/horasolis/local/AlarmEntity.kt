package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.NewAlarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import io.ktor.util.date.WeekDay
import java.time.LocalDate

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String? = null,
    val time: SolisTime,
    val enabled: Boolean = true,
    val onForWeekDays: Set<WeekDay>? = null,
    val onTimeDate: LocalDate? = null,
    val soundUri: String? = null,
    val vibrate: Boolean? = null,
) {

    fun toSavedAlarm(
        id: Long = this.id,
    ): SavedAlarm {
        val schedule = when {
            onTimeDate != null -> Alarm.Schedule.OneTime(onTimeDate)
            else -> Alarm.Schedule.Repeating(onForWeekDays ?: WeekDay.entries.toSet())
        }
        return SavedAlarm(
            id = id.toInt(),
            label = label,
            solisTime = time,
            enabled = enabled,
            schedule = schedule,
            soundUri = soundUri,
            vibrate = vibrate,
        )
    }
}

fun Alarm.toEntity(): AlarmEntity {
    val weekDays = (schedule as? Alarm.Schedule.Repeating)?.weekDays
    val oneTimeDate = (schedule as? Alarm.Schedule.OneTime)?.date
    return when (this) {
        is SavedAlarm -> AlarmEntity(
            id = id.toLong(),
            label = label,
            time = solisTime,
            enabled = enabled,
            onForWeekDays = weekDays,
            onTimeDate = oneTimeDate,
            soundUri = soundUri,
            vibrate = vibrate,
        )

        is NewAlarm -> AlarmEntity(
            label = label,
            time = solisTime,
            enabled = enabled,
            onForWeekDays = weekDays,
            onTimeDate = oneTimeDate,
            soundUri = soundUri,
            vibrate = vibrate,
        )
    }
}
