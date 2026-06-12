package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class ObserveAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<SavedAlarm?> = alarmRepository.getRingingFlow()
}
