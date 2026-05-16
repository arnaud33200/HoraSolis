package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.local.CurrentLocationEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(
    database: HoraSolisDatabase,
) {

    private val locationDao = database.locationDao()

    suspend fun setCurrentLocation(id: String) {
        locationDao.upsertCurrentLocation(CurrentLocationEntity(locationId = id))
    }

    suspend fun getCurrentLocation(): SavedLocation? {
        val locationId = locationDao.getCurrentLocation()?.locationId ?: return null
        return locationDao.get(locationId)?.toSavedLocation()
    }

    suspend fun getLocationOrNull(id: String): SavedLocation? {
        return locationDao.get(id)?.toSavedLocation()
    }

    suspend fun saveLocation(location: SavedLocation) {
        val entity = LocationEntity(
            id = location.id,
            name = location.name,
            latitude = location.lat,
            longitude = location.lng,
            zoneId = location.timZoneId,
        )
        locationDao.upsert(entity)
    }

    fun observeLocation(): Flow<SavedLocation?> {
        return locationDao.observeCurrentLocation().map { currentEntity ->
            currentEntity?.locationId?.let { id -> locationDao.get(id)?.toSavedLocation() }
        }
    }

    fun observeAllLocations(): Flow<List<SavedLocation>> {
        return locationDao.observe().map { locations ->
            locations.map { it.toSavedLocation() }
        }
    }
}
