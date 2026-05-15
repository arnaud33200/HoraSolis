package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(
    database: HoraSolisDatabase,
) {

    companion object {

        private const val CURRENT_LOCATION_ID = "current_location"
    }

    private val locationDao = database.locationDao()

    suspend fun setCurrentLocation(location: UserLocation) {
        val locationEntity = LocationEntity(
            id = CURRENT_LOCATION_ID,
            name = "",
            latitude = location.lat,
            longitude = location.lng,
            zoneId = location.timZoneId,
        )
        locationDao.upsert(locationEntity)
    }

    suspend fun getCurrentLocation(): UserLocation? {
        return locationDao.get(CURRENT_LOCATION_ID)?.toUserLocation()
    }

    fun observeLocation(): Flow<UserLocation?> {
        return locationDao.observe().map { locations ->
            locations.firstOrNull { it.id == CURRENT_LOCATION_ID }?.toUserLocation()
        }
    }

    fun observeAllLocations(): Flow<List<SavedLocation>> {
        return locationDao.observe().map { locations ->
            locations.map { it.toSavedLocation() }
        }
    }
}