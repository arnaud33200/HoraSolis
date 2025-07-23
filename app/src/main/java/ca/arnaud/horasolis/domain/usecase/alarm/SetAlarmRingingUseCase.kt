package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SavedAlarm

data class SetAlarmRingingParams(
    val alarmId: Int,
)

sealed interface SetAlarmRingingError {

    data object AlarmNotFound : SetAlarmRingingError
}

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(
        params: SetAlarmRingingParams,
    ): Response<SavedAlarm, SetAlarmRingingError> {
        val alarm = alarmRepository.getAlarm(params.alarmId)
            ?: return Response.Failure(SetAlarmRingingError.AlarmNotFound)
        alarmRepository.setAlarmRinging(params)
        return Response.Success(alarm)
    }
}