package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.repository.AlarmRepository
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmError
import ca.arnaud.horasolis.local.AlarmDao
import ca.arnaud.horasolis.local.AlarmRingingEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    database: HoraSolisDatabase,
) : AlarmRepository {

    private val alarmDao: AlarmDao = database.alarmDao()

    override suspend fun setAlarmRinging(params: SetAlarmRingingParams?) {
        if (params == null) {
            alarmDao.clearAlarmRinging()
        } else {
            val entity = AlarmRingingEntity(alarmId = params.alarmId)
            alarmDao.setAlarmRinging(entity)
        }
    }

    override fun getRingingFlow(): Flow<SavedAlarm?> =
        alarmDao.observeRinging().map { alarmRinging ->
            when (alarmRinging) {
                null -> null
                else -> {
                    val alarmId = alarmRinging.alarmId
                    alarmDao.getAlarm(alarmId)?.toSavedAlarm()
                }
            }
        }

    override suspend fun upsertAlarm(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> {
        val entity = alarm.toEntity()
        val id = alarmDao.upsertAlarm(entity)
        return Response.Success(entity.toSavedAlarm(id))
    }

    override fun getAlarmsFlow(): Flow<List<SavedAlarm>> {
        return alarmDao.observeAlarms().map { alarms ->
            alarms.map { it.toSavedAlarm() }
        }
    }

    override suspend fun getAllAlarms(): List<SavedAlarm> {
        return alarmDao.getAllAlarms().map { it.toSavedAlarm() }
    }

    override suspend fun getAlarmOrNull(id: Int): SavedAlarm? {
        return alarmDao.getAlarm(id)?.toSavedAlarm()
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        alarmDao.deleteAlarm(alarmId)
    }
}
