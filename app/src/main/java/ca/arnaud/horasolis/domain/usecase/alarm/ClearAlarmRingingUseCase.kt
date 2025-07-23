package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

class ClearAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke() {
        alarmRepository.setAlarmRinging(null)
    }
}