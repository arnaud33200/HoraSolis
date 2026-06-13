package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.local.LogLocalDataSource
import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(
    private val timeProvider: TimeProvider,
    private val localDataSource: LogLocalDataSource,
) : LogRepository {

    override suspend fun saveAlarmLog(alarmLog: SaveAlarmLogParam) =
        localDataSource.insertLog(alarmLog, timeProvider.getNowDateTime())

    override fun getLogsFlow(): Flow<List<AlarmLog>> = localDataSource.observeLogs()

    override suspend fun clearLogs() = localDataSource.deleteAllLogs()
}
