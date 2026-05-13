package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.onSuccess
import ca.arnaud.horasolis.domain.provider.TimeProvider

data object UpsertAlarmError

class UpsertAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val scheduleSolisAlarm: ScheduleSolisAlarmUseCase,
    private val timeProvider: TimeProvider,
) {

    suspend operator fun invoke(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> {
        return alarmRepository.upsertAlarm(alarm).onSuccess { savedAlarm ->
            scheduleSolisAlarm(savedAlarm, timeProvider.getNowDate())
        }
    }
}