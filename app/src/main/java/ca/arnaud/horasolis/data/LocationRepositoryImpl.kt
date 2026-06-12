package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.repository.LocationRepository
import ca.arnaud.horasolis.local.CurrentLocationEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl(
    database: HoraSolisDatabase,
) : LocationRepository {

    private val locationDao = database.locationDao()

    override suspend fun setCurrentLocation(id: String) {
        locationDao.upsertCurrentLocation(CurrentLocationEntity(locationId = id))
    }

    override suspend fun getCurrentLocationOrNull(): SavedLocation? {
        val locationId = locationDao.getCurrentLocation()?.locationId ?: return null
        return locationDao.get(locationId)?.toSavedLocation()
    }

    override suspend fun getLocationOrNull(id: String): SavedLocation? {
        return locationDao.get(id)?.toSavedLocation()
    }

    override suspend fun getAllLocations(): List<SavedLocation> {
        return locationDao.getAll().map { it.toSavedLocation() }
    }

    override suspend fun deleteLocation(id: String) {
        locationDao.deleteById(id)
    }

    override suspend fun saveLocation(location: SavedLocation) {
        val entity = LocationEntity(
            id = location.id,
            name = location.name,
            latitude = location.lat,
            longitude = location.lng,
            zoneId = location.timZoneId,
        )
        locationDao.upsert(entity)
    }

    override fun observeLocation(): Flow<SavedLocation?> {
        return locationDao.observeCurrentLocation().map { currentEntity ->
            currentEntity?.locationId?.let { id -> locationDao.get(id)?.toSavedLocation() }
        }
    }

    override fun observeAllLocations(): Flow<List<SavedLocation>> {
        return locationDao.observe().map { locations ->
            locations.map { it.toSavedLocation() }
        }
    }
}
