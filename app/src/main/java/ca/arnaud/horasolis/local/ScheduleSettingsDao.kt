package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: ScheduleSettingsEntity)

    @Update
    suspend fun update(settings: ScheduleSettingsEntity)

    @Query("SELECT * FROM schedule_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ScheduleSettingsEntity?

    @Query("DELETE FROM schedule_settings")
    suspend fun clear()
}
