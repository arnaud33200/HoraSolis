package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.arnaud.horasolis.domain.usecase.alarm.AlarmRinging
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAlarmRinging(entity: AlarmRingingEntity)

    @Query("DELETE FROM alarm_ringing")
    suspend fun clearAlarmRinging()

    @Query("SELECT * FROM alarm_ringing WHERE id = 1")
    fun observeRinging(): Flow<AlarmRinging?>

    @Query("SELECT * FROM alarm ORDER BY time ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm WHERE id = :id LIMIT 1")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}

