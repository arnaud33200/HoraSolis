package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

data class SetAlarmRingingParams(
    val number: Int,
)

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(params: SetAlarmRingingParams?) {
        alarmRepository.setAlarmRinging(params)
    }
}