package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.ScheduleLocalDataSource
import ca.arnaud.horasolis.domain.model.ScheduledAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleLocalDataSourceImpl(database: HoraSolisDatabase) : ScheduleLocalDataSource {

    private val alarmScheduleDao = database.alarmScheduleDao()

    override suspend fun upsertSchedule(alarm: ScheduledAlarm) =
        alarmScheduleDao.upsertSchedule(alarm.toEntity())

    override suspend fun deleteSchedule(alarmId: Int) = alarmScheduleDao.deleteSchedule(alarmId)

    override suspend fun deleteAllSchedules() = alarmScheduleDao.deleteAllSchedules()

    override suspend fun getSchedule(alarmId: Int): ScheduledAlarm? =
        alarmScheduleDao.getSchedule(alarmId)?.toScheduledAlarm()

    override suspend fun getAllSchedules(): List<ScheduledAlarm> =
        alarmScheduleDao.getAllSchedules().map { it.toScheduledAlarm() }

    override fun observeSchedules(): Flow<List<ScheduledAlarm>> =
        alarmScheduleDao.observeSchedules().map { entities -> entities.map { it.toScheduledAlarm() } }
}
