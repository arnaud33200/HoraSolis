package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.repository.AlarmRepository

class ClearAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke() {
        alarmRepository.setAlarmRinging(null)
    }
}
