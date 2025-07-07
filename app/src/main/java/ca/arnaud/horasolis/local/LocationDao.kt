package ca.arnaud.horasolis.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE id = :id LIMIT 1")
    suspend fun get(id : String): LocationEntity?

    @Query("SELECT * FROM location")
    suspend fun getAll(): List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: LocationEntity)

    @Update
    suspend fun update(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)
}

