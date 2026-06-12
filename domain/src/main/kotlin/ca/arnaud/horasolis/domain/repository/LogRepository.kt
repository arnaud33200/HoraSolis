package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    suspend fun saveAlarmLog(alarmLog: SaveAlarmLogParam)
    fun getLogsFlow(): Flow<List<AlarmLog>>
    suspend fun clearLogs()
}
