package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.local.AlarmLocalDataSource
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.repository.AlarmRepository
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmError
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(
    private val localDataSource: AlarmLocalDataSource,
) : AlarmRepository {

    override suspend fun setAlarmRinging(params: SetAlarmRingingParams?) {
        if (params == null) {
            localDataSource.clearAlarmRinging()
        } else {
            localDataSource.setAlarmRinging(params.alarmId)
        }
    }

    override fun getRingingFlow(): Flow<SavedAlarm?> = localDataSource.observeRinging()

    override suspend fun upsertAlarm(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> =
        Response.Success(localDataSource.upsertAlarm(alarm))

    override fun getAlarmsFlow(): Flow<List<SavedAlarm>> = localDataSource.observeAlarms()

    override suspend fun getAllAlarms(): List<SavedAlarm> = localDataSource.getAllAlarms()

    override suspend fun getAlarmOrNull(id: Int): SavedAlarm? = localDataSource.getAlarm(id)

    override suspend fun deleteAlarm(alarmId: Int) = localDataSource.deleteAlarm(alarmId)
}
