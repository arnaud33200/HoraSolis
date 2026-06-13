package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.LogLocalDataSource
import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class LogLocalDataSourceImpl(database: HoraSolisDatabase) : LogLocalDataSource {

    private val alarmLogDao = database.alarmLogDao()

    override suspend fun insertLog(param: SaveAlarmLogParam, dateTime: LocalDateTime) =
        alarmLogDao.insertLog(param.toEntity(dateTime))

    override fun observeLogs(): Flow<List<AlarmLog>> =
        alarmLogDao.observeLogs().map { entities -> entities.mapNotNull { it.toAlarmLogOrNull() } }

    override suspend fun deleteAllLogs() = alarmLogDao.deleteAllLogs()
}
