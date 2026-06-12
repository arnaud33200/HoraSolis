package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.NewAlarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.model.common.onFailure
import ca.arnaud.horasolis.domain.model.common.onSuccess
import ca.arnaud.horasolis.domain.provider.Logger
import ca.arnaud.horasolis.domain.repository.AlarmRepository

data object UpsertAlarmError

class UpsertAlarmException : Exception("UpsertAlarm failed")

class UpsertAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val scheduleNextAlarm: ScheduleNextAlarmUseCase,
    private val logger: Logger,
) {

    suspend operator fun invoke(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> {
        val alarmToSave = when (alarm) {
            is NewAlarm -> alarm.copy(enabled = true)
            is SavedAlarm -> alarm
        }
        return alarmRepository.upsertAlarm(alarmToSave).onSuccess { savedAlarm ->
            scheduleNextAlarm(savedAlarm)
        }.onFailure {
            logger.logNonFatal(UpsertAlarmException())
        }
    }
}
