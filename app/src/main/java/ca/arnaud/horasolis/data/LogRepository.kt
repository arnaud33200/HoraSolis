package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogRepository(
    private val timeProvider: TimeProvider,
    database: HoraSolisDatabase,
) {

    private val alarmLogDao = database.alarmLogDao()

    suspend fun saveAlarmLog(alarmLog: SaveAlarmLogParam) {
        alarmLogDao.insertLog(alarmLog.toEntity(timeProvider.getNowDateTime()))
    }

    fun getLogsFlow(): Flow<List<AlarmLog>> =
        alarmLogDao.observeLogs().map { entities ->
            entities.mapNotNull { it.toAlarmLogOrNull() }
        }
}
