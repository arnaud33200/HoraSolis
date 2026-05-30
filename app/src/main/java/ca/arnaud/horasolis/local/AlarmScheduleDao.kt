package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmScheduleDao {

    @Upsert
    suspend fun upsertSchedule(entity: AlarmScheduleEntity)

    @Query("DELETE FROM alarm_schedule WHERE alarmId = :alarmId")
    suspend fun deleteSchedule(alarmId: Int)

    @Query("DELETE FROM alarm_schedule")
    suspend fun deleteAllSchedules()

    @Query("SELECT * FROM alarm_schedule WHERE alarmId = :alarmId LIMIT 1")
    suspend fun getSchedule(alarmId: Int): AlarmScheduleEntity?

    @Query("SELECT * FROM alarm_schedule")
    suspend fun getAllSchedules(): List<AlarmScheduleEntity>

    @Query("SELECT * FROM alarm_schedule ORDER BY scheduledDateTime ASC")
    fun observeSchedules(): Flow<List<AlarmScheduleEntity>>
}
