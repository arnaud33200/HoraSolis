package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String? = null,
    val time: LocalTime,
    val enabled: Boolean = true,
)
