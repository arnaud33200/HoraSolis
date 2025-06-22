package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.data.AlarmRepository

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(ringing: Boolean) {
        alarmRepository.setAlarmRinging(ringing)
    }
}