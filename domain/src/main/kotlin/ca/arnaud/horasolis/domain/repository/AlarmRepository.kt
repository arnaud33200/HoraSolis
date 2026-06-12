package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmError
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun setAlarmRinging(params: SetAlarmRingingParams?)
    fun getRingingFlow(): Flow<SavedAlarm?>
    suspend fun upsertAlarm(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError>
    fun getAlarmsFlow(): Flow<List<SavedAlarm>>
    suspend fun getAllAlarms(): List<SavedAlarm>
    suspend fun getAlarmOrNull(id: Int): SavedAlarm?
    suspend fun deleteAlarm(alarmId: Int)
}
