package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_location")
data class CurrentLocationEntity(
    @PrimaryKey val id: Int = 1,
    val locationId: String,
)
