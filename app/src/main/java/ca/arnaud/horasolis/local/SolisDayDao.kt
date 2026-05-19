package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SolisDayDao {

    @Query("SELECT * FROM solis_day WHERE cacheKey = :key")
    suspend fun get(key: String): SolisDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SolisDayEntity)
}
