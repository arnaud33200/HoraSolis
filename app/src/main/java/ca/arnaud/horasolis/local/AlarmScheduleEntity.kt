package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.data.model.ScheduledAlarm
import java.time.LocalDateTime

@Entity(tableName = "alarm_schedule")
data class AlarmScheduleEntity(
    @PrimaryKey val alarmId: Int,
    val scheduledDateTime: LocalDateTime,
) {

    fun toScheduledAlarm(): ScheduledAlarm = ScheduledAlarm(
        alarmId = alarmId,
        scheduledDateTime = scheduledDateTime,
    )
}

fun ScheduledAlarm.toEntity(): AlarmScheduleEntity = AlarmScheduleEntity(
    alarmId = alarmId,
    scheduledDateTime = scheduledDateTime,
)
