package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.ScheduledAlarm
import ca.arnaud.horasolis.domain.repository.ScheduleRepository
import ca.arnaud.horasolis.local.AlarmScheduleEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.service.SolisTimeAlarmScheduleParam
import ca.arnaud.horasolis.service.SolisTimeAlarmService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ScheduleRepositoryImpl(
    private val alarmService: SolisTimeAlarmService,
    database: HoraSolisDatabase,
) : ScheduleRepository {

    private val alarmScheduleDao = database.alarmScheduleDao()

    override suspend fun hasScheduledAlarm(alarmId: Int): Boolean =
        alarmScheduleDao.getSchedule(alarmId) != null

    override suspend fun getAllScheduledAlarms(): List<ScheduledAlarm> =
        alarmScheduleDao.getAllSchedules().map { it.toScheduledAlarm() }

    override fun getScheduledAlarmsFlow(): Flow<List<ScheduledAlarm>> =
        alarmScheduleDao.observeSchedules().map { entities ->
            entities.map { it.toScheduledAlarm() }
        }

    override suspend fun scheduleAlarm(alarmId: Int, dateTime: LocalDateTime) {
        alarmService.scheduleAlarm(SolisTimeAlarmScheduleParam(alarmId = alarmId, dateTime = dateTime))
        alarmScheduleDao.upsertSchedule(
            AlarmScheduleEntity(
                alarmId = alarmId,
                scheduledDateTime = dateTime,
            )
        )
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        alarmService.cancelAlarm(alarmId)
        alarmScheduleDao.deleteSchedule(alarmId)
    }

    override suspend fun cancelAll() {
        alarmService.cancelAll()
        alarmScheduleDao.deleteAllSchedules()
    }
}
