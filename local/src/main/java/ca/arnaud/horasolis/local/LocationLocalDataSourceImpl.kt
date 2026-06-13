package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.LocationLocalDataSource
import ca.arnaud.horasolis.domain.model.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationLocalDataSourceImpl(database: HoraSolisDatabase) : LocationLocalDataSource {

    private val locationDao = database.locationDao()

    override suspend fun setCurrentLocationId(locationId: String) =
        locationDao.upsertCurrentLocation(CurrentLocationEntity(locationId = locationId))

    override suspend fun getCurrentLocation(): SavedLocation? {
        val locationId = locationDao.getCurrentLocation()?.locationId ?: return null
        return locationDao.get(locationId)?.toSavedLocation()
    }

    override suspend fun getLocation(id: String): SavedLocation? =
        locationDao.get(id)?.toSavedLocation()

    override suspend fun getAllLocations(): List<SavedLocation> =
        locationDao.getAll().map { it.toSavedLocation() }

    override suspend fun deleteLocation(id: String) = locationDao.deleteById(id)

    override suspend fun upsertLocation(location: SavedLocation) =
        locationDao.upsert(
            LocationEntity(
                id = location.id,
                name = location.name,
                latitude = location.lat,
                longitude = location.lng,
                zoneId = location.timZoneId,
            )
        )

    override fun observeCurrentLocation(): Flow<SavedLocation?> =
        locationDao.observeCurrentLocation().map { currentEntity ->
            currentEntity?.locationId?.let { id -> locationDao.get(id)?.toSavedLocation() }
        }

    override fun observeAllLocations(): Flow<List<SavedLocation>> =
        locationDao.observe().map { locations -> locations.map { it.toSavedLocation() } }
}
