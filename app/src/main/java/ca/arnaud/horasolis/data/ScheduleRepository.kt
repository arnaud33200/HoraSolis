package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.model.ScheduledAlarm
import ca.arnaud.horasolis.local.AlarmScheduleEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.service.SolisTimeAlarmScheduleParam
import ca.arnaud.horasolis.service.SolisTimeAlarmService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository(
    private val alarmService: SolisTimeAlarmService,
    database: HoraSolisDatabase,
) {

    private val alarmScheduleDao = database.alarmScheduleDao()

    suspend fun getScheduledAlarmOrNull(alarmId: Int): ScheduledAlarm? =
        alarmScheduleDao.getSchedule(alarmId)?.toScheduledAlarm()

    fun getScheduledAlarmsFlow(): Flow<List<ScheduledAlarm>> =
        alarmScheduleDao.observeSchedules().map { entities ->
            entities.map { it.toScheduledAlarm() }
        }

    suspend fun scheduleAlarm(param: SolisTimeAlarmScheduleParam) {
        alarmService.scheduleAlarm(param)
        alarmScheduleDao.upsertSchedule(
            AlarmScheduleEntity(
                alarmId = param.alarmId,
                scheduledDateTime = param.dateTime,
            )
        )
    }

    suspend fun cancelAlarm(alarmId: Int) {
        alarmService.cancelAlarm(alarmId)
        alarmScheduleDao.deleteSchedule(alarmId)
    }

    suspend fun cancelAll() {
        alarmService.cancelAll()
        alarmScheduleDao.deleteAllSchedules()
    }
}
