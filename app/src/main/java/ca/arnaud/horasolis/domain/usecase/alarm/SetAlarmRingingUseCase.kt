package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

data class SetAlarmRingingParams(
    val alarmId: Int,
)

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(params: SetAlarmRingingParams?) {
        alarmRepository.setAlarmRinging(params)
    }
}