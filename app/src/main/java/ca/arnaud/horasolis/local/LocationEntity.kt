package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.SavedLocation

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zoneId: String,
) {

    fun toSavedLocation(): SavedLocation {
        return SavedLocation(
            id = id,
            name = name,
            lat = latitude,
            lng = longitude,
            timZoneId = zoneId,
        )
    }
}
