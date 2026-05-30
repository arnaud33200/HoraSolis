package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmLogDao {

    @Insert
    suspend fun insertLog(entity: AlarmLogEntity)

    @Query("SELECT * FROM alarm_log ORDER BY dateTime DESC")
    fun observeLogs(): Flow<List<AlarmLogEntity>>

    @Query("SELECT * FROM alarm_log WHERE alarmId = :alarmId ORDER BY dateTime DESC")
    fun observeLogsByAlarmId(alarmId: Int): Flow<List<AlarmLogEntity>>
}
