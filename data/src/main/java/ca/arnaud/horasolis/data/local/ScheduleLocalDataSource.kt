package ca.arnaud.horasolis.data.local

import ca.arnaud.horasolis.domain.model.ScheduledAlarm
import kotlinx.coroutines.flow.Flow

interface ScheduleLocalDataSource {

    suspend fun upsertSchedule(alarm: ScheduledAlarm)
    suspend fun deleteSchedule(alarmId: Int)
    suspend fun deleteAllSchedules()
    suspend fun getSchedule(alarmId: Int): ScheduledAlarm?
    suspend fun getAllSchedules(): List<ScheduledAlarm>
    fun observeSchedules(): Flow<List<ScheduledAlarm>>
}
