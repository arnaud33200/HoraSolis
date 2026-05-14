package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.NewAlarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import io.ktor.util.date.WeekDay

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String? = null,
    val time: SolisTime,
    val enabled: Boolean = true,
    val onForWeekDays: Set<WeekDay>? = null,
) {

    fun toSavedAlarm(
        id: Long = this.id,
    ): SavedAlarm {
        return SavedAlarm(
            id = id.toInt(),
            label = label,
            solisTime = time,
            enabled = enabled,
            onForWeekDays = onForWeekDays ?: WeekDay.entries.toSet(),
        )
    }
}

fun Alarm.toEntity(): AlarmEntity {
    return when (this) {
        is SavedAlarm -> AlarmEntity(
            id = id.toLong(),
            label = label,
            time = solisTime,
            enabled = enabled,
            onForWeekDays = onForWeekDays,
        )

        is NewAlarm -> AlarmEntity(
            label = label,
            time = solisTime,
            enabled = enabled,
            onForWeekDays = onForWeekDays,
        )
    }
}
