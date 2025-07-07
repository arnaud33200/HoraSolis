package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.usecase.alarm.AlarmRinging
import kotlinx.coroutines.flow.Flow

class ObserveAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<AlarmRinging?> = alarmRepository.getRingingFlow()
}