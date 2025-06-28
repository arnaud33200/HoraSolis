package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsWithTimesDao {
    // ScheduleSettings operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: ScheduleSettingsEntity)

    @Update
    suspend fun updateSettings(settings: ScheduleSettingsEntity)

    @Query("SELECT * FROM schedule_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ScheduleSettingsEntity?

    @Query("SELECT * FROM schedule_settings")
    fun observeAllSettings(): Flow<List<ScheduleSettingsEntity>>

    @Query("DELETE FROM schedule_settings")
    suspend fun clearSettings()

    @Insert
    suspend fun insertTime(time: SelectedTimeEntity)

    @Query("SELECT * FROM selected_times")
    suspend fun getAllTimes(): List<SelectedTimeEntity>

    @Delete
    suspend fun deleteTime(time: SelectedTimeEntity)

    @Query("DELETE FROM selected_times")
    suspend fun deleteAllTimes()

    @Query("SELECT * FROM selected_times")
    fun observeAllTimes(): Flow<List<SelectedTimeEntity>>

    @Transaction
    suspend fun saveSettingsAndTimes(
        settings: ScheduleSettingsEntity,
        times: List<SelectedTimeEntity>,
    ) {
        insertSettings(settings)
        deleteAllTimes()
        times.forEach { insertTime(it) }
    }

    @Transaction
    suspend fun getSettingsAggregate(): ScheduleSettingsAggregate {
        val settings = getSettings()
        val times = getAllTimes()
        return ScheduleSettingsAggregate(settings, times)
    }

    @Transaction
    @Query("SELECT * FROM schedule_settings WHERE id = 1")
    fun observeSettingsAggregate(): Flow<ScheduleSettingsAggregate?>
}
