package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.LocationEntity

class LocationRepository(
    database: HoraSolisDatabase,
) {

    companion object {

        private const val CURRENT_LOCATION_ID = "current_location"
    }

    private val settingsDao = database.locationDao()

    suspend fun setCurrentLocation(location: UserLocation) {
        val locationEntity = LocationEntity(
            id = CURRENT_LOCATION_ID,
            name = "",
            latitude = location.lat,
            longitude = location.lng,
            zoneId = location.timZoneId,
        )
        settingsDao.upsert(locationEntity)
    }

    suspend fun getCurrentLocation(): UserLocation? {
        val locationEntity = settingsDao.get(CURRENT_LOCATION_ID) ?: return null
        return UserLocation(
            lat = locationEntity.latitude,
            lng = locationEntity.longitude,
            timZoneId = locationEntity.zoneId,
        )
    }
}