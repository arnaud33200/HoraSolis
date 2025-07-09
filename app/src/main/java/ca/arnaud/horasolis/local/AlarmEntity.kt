package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.Alarm
import ca.arnaud.horasolis.domain.model.NewAlarm
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisTime
import java.time.LocalTime

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String? = null,
    val time: SolisTime,
    val enabled: Boolean = true,
) {

    fun toSavedAlarm(): SavedAlarm {
        return SavedAlarm(
            id = id,
            label = label,
            solisTime = time,
            enabled = enabled,
        )
    }
}

fun Alarm.toEntity(): AlarmEntity {
    return when (this) {
        is SavedAlarm -> return AlarmEntity(
            id = id,
            label = label,
            time = solisTime,
            enabled = enabled,
        )
        is NewAlarm -> return AlarmEntity(
            label = label,
            time = solisTime,
            enabled = enabled,
        )
    }
}
