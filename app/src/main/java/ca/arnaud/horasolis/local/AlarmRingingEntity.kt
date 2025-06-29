package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_ringing")
data class AlarmRingingEntity(
    @PrimaryKey val id: Int = 1,
    val number: Int,
)


