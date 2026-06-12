package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.SavedLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun setCurrentLocation(id: String)
    suspend fun getCurrentLocationOrNull(): SavedLocation?
    suspend fun getLocationOrNull(id: String): SavedLocation?
    suspend fun getAllLocations(): List<SavedLocation>
    suspend fun deleteLocation(id: String)
    suspend fun saveLocation(location: SavedLocation)
    fun observeLocation(): Flow<SavedLocation?>
    fun observeAllLocations(): Flow<List<SavedLocation>>
}
