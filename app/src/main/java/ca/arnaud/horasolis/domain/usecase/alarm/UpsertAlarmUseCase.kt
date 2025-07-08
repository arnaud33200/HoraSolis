package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.Alarm
import ca.arnaud.horasolis.domain.model.SavedAlarm

data object UpsertAlarmError

class UpsertAlarmUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(alarm: Alarm): Response<SavedAlarm, UpsertAlarmError> {
        return alarmRepository.upsertAlarm(alarm)
    }
}