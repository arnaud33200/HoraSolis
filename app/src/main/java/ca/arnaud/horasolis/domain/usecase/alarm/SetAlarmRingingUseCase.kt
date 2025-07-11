package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

data class AlarmRinging(
    val number: Int,
)

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(params: AlarmRinging?) {
        alarmRepository.setAlarmRinging(params)
    }
}