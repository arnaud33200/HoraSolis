package ca.arnaud.horasolis.data.local

import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {
    suspend fun setAlarmRinging(alarmId: Int)
    suspend fun clearAlarmRinging()
    fun observeRinging(): Flow<SavedAlarm?>
    fun observeAlarms(): Flow<List<SavedAlarm>>
    suspend fun getAllAlarms(): List<SavedAlarm>
    suspend fun getAlarm(id: Int): SavedAlarm?
    suspend fun upsertAlarm(alarm: Alarm): SavedAlarm
    suspend fun deleteAlarm(alarmId: Int)
}
