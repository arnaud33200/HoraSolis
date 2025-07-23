package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAlarmRinging(entity: AlarmRingingEntity)

    @Query("DELETE FROM alarm_ringing")
    suspend fun clearAlarmRinging()

    @Query("SELECT * FROM alarm_ringing WHERE id = 1")
    suspend fun getAlarmRinging(): AlarmRingingEntity?

    @Query("SELECT * FROM alarm_ringing WHERE id = 1")
    fun observeRinging(): Flow<AlarmRingingEntity?>

    @Query("SELECT * FROM alarm ORDER BY time ASC")
    fun observeAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm WHERE id = :alarmId")
    suspend fun getAlarm(alarmId: Int): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlarm(alarm: AlarmEntity): Long

    @Query("DELETE FROM alarm WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Int)


}
