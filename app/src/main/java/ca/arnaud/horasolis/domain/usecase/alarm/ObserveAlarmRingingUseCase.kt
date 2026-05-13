package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import kotlinx.coroutines.flow.Flow

class ObserveAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<SavedAlarm?> = alarmRepository.getRingingFlow()
}