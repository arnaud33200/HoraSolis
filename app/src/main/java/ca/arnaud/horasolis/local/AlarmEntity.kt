package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.Alarm
import ca.arnaud.horasolis.domain.model.NewAlarm
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisTime

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String? = null,
    val time: SolisTime,
    val enabled: Boolean = true,
) {

    fun toSavedAlarm(
        id: Long = this.id,
    ): SavedAlarm {
        return SavedAlarm(
            id = id.toInt(),
            label = label,
            solisTime = time,
            enabled = enabled,
        )
    }
}

fun Alarm.toEntity(): AlarmEntity {
    return when (this) {
        is SavedAlarm -> return AlarmEntity(
            id = id.toLong(),
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
