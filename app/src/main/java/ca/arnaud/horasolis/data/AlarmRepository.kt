package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.Alarm
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.usecase.alarm.AlarmRinging
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmError
import ca.arnaud.horasolis.local.AlarmDao
import ca.arnaud.horasolis.local.AlarmRingingEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepository(
    database: HoraSolisDatabase,
) {

    private val alarmDao: AlarmDao = database.alarmDao()

    suspend fun setAlarmRinging(params: AlarmRinging?) {
        if (params == null) {
            alarmDao.clearAlarmRinging()
        } else {
            val entity = AlarmRingingEntity(number = params.number)
            alarmDao.setAlarmRinging(entity)
        }
    }

    fun getRingingFlow(): Flow<AlarmRinging?> =
        alarmDao.observeRinging()

    suspend fun upsertAlarm(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> {
        val entity = alarm.toEntity()
        alarmDao.upsertAlarm(entity)
        return Response.Success(entity.toSavedAlarm())
    }

    fun getAlarmsFlow(): Flow<List<SavedAlarm>> {
        return alarmDao.observeAlarms().map { alarms ->
            alarms.map { it.toSavedAlarm() }
        }
    }

    suspend fun getAlarm(id: Int): SavedAlarm? {
        return alarmDao.getAlarm(id)?.toSavedAlarm()
    }

    suspend fun deleteAlarm(alarmId: Int) {
        alarmDao.deleteAlarm(alarmId)
    }
}