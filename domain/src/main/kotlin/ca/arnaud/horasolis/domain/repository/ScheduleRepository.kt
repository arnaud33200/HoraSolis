package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.ScheduledAlarm
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ScheduleRepository {
    suspend fun hasScheduledAlarm(alarmId: Int): Boolean
    suspend fun getAllScheduledAlarms(): List<ScheduledAlarm>
    fun getScheduledAlarmsFlow(): Flow<List<ScheduledAlarm>>
    suspend fun scheduleAlarm(alarmId: Int, dateTime: LocalDateTime)
    suspend fun cancelAlarm(alarmId: Int)
    suspend fun cancelAll()
}
