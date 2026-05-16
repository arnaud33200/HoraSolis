package ca.arnaud.horasolis.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE id = :id LIMIT 1")
    suspend fun get(id: String): LocationEntity?

    @Query("SELECT * FROM location")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM location")
    fun observe(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: LocationEntity)

    @Update
    suspend fun update(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrentLocation(entity: CurrentLocationEntity)

    @Query("SELECT * FROM current_location WHERE id = 1 LIMIT 1")
    suspend fun getCurrentLocation(): CurrentLocationEntity?

    @Query("SELECT * FROM current_location WHERE id = 1 LIMIT 1")
    fun observeCurrentLocation(): Flow<CurrentLocationEntity?>
}

