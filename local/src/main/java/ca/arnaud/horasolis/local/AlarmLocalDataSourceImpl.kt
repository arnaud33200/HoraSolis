package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.AlarmLocalDataSource
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmLocalDataSourceImpl(database: HoraSolisDatabase) : AlarmLocalDataSource {

    private val alarmDao = database.alarmDao()

    override suspend fun setAlarmRinging(alarmId: Int) =
        alarmDao.setAlarmRinging(AlarmRingingEntity(alarmId = alarmId))

    override suspend fun clearAlarmRinging() = alarmDao.clearAlarmRinging()

    override fun observeRinging(): Flow<SavedAlarm?> =
        alarmDao.observeRinging().map { alarmRinging ->
            alarmRinging?.alarmId?.let { id -> alarmDao.getAlarm(id)?.toSavedAlarm() }
        }

    override fun observeAlarms(): Flow<List<SavedAlarm>> =
        alarmDao.observeAlarms().map { alarms -> alarms.map { it.toSavedAlarm() } }

    override suspend fun getAllAlarms(): List<SavedAlarm> =
        alarmDao.getAllAlarms().map { it.toSavedAlarm() }

    override suspend fun getAlarm(id: Int): SavedAlarm? = alarmDao.getAlarm(id)?.toSavedAlarm()

    override suspend fun upsertAlarm(alarm: Alarm): SavedAlarm {
        val entity = alarm.toEntity()
        val id = alarmDao.upsertAlarm(entity)
        return entity.toSavedAlarm(id)
    }

    override suspend fun deleteAlarm(alarmId: Int) = alarmDao.deleteAlarm(alarmId)
}
