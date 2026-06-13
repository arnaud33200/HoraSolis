package ca.arnaud.horasolis.data.local

import ca.arnaud.horasolis.domain.model.SavedLocation
import kotlinx.coroutines.flow.Flow

interface LocationLocalDataSource {
    suspend fun setCurrentLocationId(locationId: String)
    suspend fun getCurrentLocation(): SavedLocation?
    suspend fun getLocation(id: String): SavedLocation?
    suspend fun getAllLocations(): List<SavedLocation>
    suspend fun deleteLocation(id: String)
    suspend fun upsertLocation(location: SavedLocation)
    fun observeCurrentLocation(): Flow<SavedLocation?>
    fun observeAllLocations(): Flow<List<SavedLocation>>
}
