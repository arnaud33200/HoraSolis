package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.local.ScheduleLocalDataSource
import ca.arnaud.horasolis.domain.model.ScheduledAlarm
import ca.arnaud.horasolis.domain.provider.AlarmScheduler
import ca.arnaud.horasolis.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ScheduleRepositoryImpl(
    private val alarmScheduler: AlarmScheduler,
    private val localDataSource: ScheduleLocalDataSource,
) : ScheduleRepository {

    override suspend fun hasScheduledAlarm(alarmId: Int): Boolean =
        localDataSource.getSchedule(alarmId) != null

    override suspend fun getAllScheduledAlarms(): List<ScheduledAlarm> =
        localDataSource.getAllSchedules()

    override fun getScheduledAlarmsFlow(): Flow<List<ScheduledAlarm>> =
        localDataSource.observeSchedules()

    override suspend fun scheduleAlarm(alarmId: Int, dateTime: LocalDateTime) {
        alarmScheduler.scheduleAlarm(alarmId = alarmId, dateTime = dateTime)
        localDataSource.upsertSchedule(ScheduledAlarm(alarmId = alarmId, scheduledDateTime = dateTime))
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        alarmScheduler.cancelAlarm(alarmId)
        localDataSource.deleteSchedule(alarmId)
    }

    override suspend fun cancelAll() {
        alarmScheduler.cancelAll()
        localDataSource.deleteAllSchedules()
    }
}
