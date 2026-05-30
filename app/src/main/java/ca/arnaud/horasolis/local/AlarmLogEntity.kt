package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import java.time.LocalDateTime

@Entity(tableName = "alarm_log")
data class AlarmLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alarmId: Int,
    val action: String,
    val dateTime: LocalDateTime,
    val scheduledDateTime: LocalDateTime? = null,
) {

    companion object {
        const val ACTION_SCHEDULED = "SCHEDULED"
        const val ACTION_CANCELLED = "CANCELLED"
        const val ACTION_RINGING = "RINGING"
    }

    fun toAlarmLogOrNull(): AlarmLog? = when (action) {
        ACTION_SCHEDULED -> AlarmLog.Scheduled(
            id = id,
            alarmId = alarmId,
            timestamp = dateTime,
            scheduledDateTime = requireNotNull(scheduledDateTime),
        )
        ACTION_CANCELLED -> AlarmLog.Cancelled(id = id, alarmId = alarmId, timestamp = dateTime)
        ACTION_RINGING -> AlarmLog.Ringing(id = id, alarmId = alarmId, timestamp = dateTime)
        else -> null
    }
}

fun SaveAlarmLogParam.toEntity(dateTime: LocalDateTime): AlarmLogEntity = when (this) {
    is SaveAlarmLogParam.Scheduled -> AlarmLogEntity(
        alarmId = alarmId,
        action = AlarmLogEntity.ACTION_SCHEDULED,
        dateTime = dateTime,
        scheduledDateTime = scheduledDateTime,
    )
    is SaveAlarmLogParam.Cancelled -> AlarmLogEntity(
        alarmId = alarmId,
        action = AlarmLogEntity.ACTION_CANCELLED,
        dateTime = dateTime,
    )
    is SaveAlarmLogParam.Ringing -> AlarmLogEntity(
        alarmId = alarmId,
        action = AlarmLogEntity.ACTION_RINGING,
        dateTime = dateTime,
    )
}
