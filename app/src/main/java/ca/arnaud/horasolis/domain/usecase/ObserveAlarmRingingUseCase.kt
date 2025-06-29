package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.AlarmRepository
import kotlinx.coroutines.flow.Flow

class ObserveAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<AlarmRinging?> = alarmRepository.getRingingFlow()
}