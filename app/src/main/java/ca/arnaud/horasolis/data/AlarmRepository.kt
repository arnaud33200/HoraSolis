package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.usecase.AlarmRinging
import ca.arnaud.horasolis.local.AlarmDao
import ca.arnaud.horasolis.local.AlarmRingingEntity
import ca.arnaud.horasolis.local.HoraSolisDatabase
import kotlinx.coroutines.flow.Flow

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
}