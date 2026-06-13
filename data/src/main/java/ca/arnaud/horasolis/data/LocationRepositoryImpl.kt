package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.local.LocationLocalDataSource
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl(
    private val localDataSource: LocationLocalDataSource,
) : LocationRepository {

    override suspend fun setCurrentLocation(id: String) = localDataSource.setCurrentLocationId(id)

    override suspend fun getCurrentLocationOrNull(): SavedLocation? =
        localDataSource.getCurrentLocation()

    override suspend fun getLocationOrNull(id: String): SavedLocation? =
        localDataSource.getLocation(id)

    override suspend fun getAllLocations(): List<SavedLocation> = localDataSource.getAllLocations()

    override suspend fun deleteLocation(id: String) = localDataSource.deleteLocation(id)

    override suspend fun saveLocation(location: SavedLocation) = localDataSource.upsertLocation(location)

    override fun observeLocation(): Flow<SavedLocation?> = localDataSource.observeCurrentLocation()

    override fun observeAllLocations(): Flow<List<SavedLocation>> = localDataSource.observeAllLocations()
}
