package ca.arnaud.horasolis.data.local

import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface LogLocalDataSource {
    suspend fun insertLog(param: SaveAlarmLogParam, dateTime: LocalDateTime)
    fun observeLogs(): Flow<List<AlarmLog>>
    suspend fun deleteAllLogs()
}
