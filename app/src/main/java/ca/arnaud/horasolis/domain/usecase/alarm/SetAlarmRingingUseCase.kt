package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.LogRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm

data class SetAlarmRingingParams(
    val alarmId: Int,
)

sealed interface SetAlarmRingingError {

    data object AlarmNotFound : SetAlarmRingingError
}

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke(
        params: SetAlarmRingingParams,
    ): Response<SavedAlarm, SetAlarmRingingError> {
        val alarm = alarmRepository.getAlarmOrNull(params.alarmId)
            ?: return Response.Failure(SetAlarmRingingError.AlarmNotFound)
        alarmRepository.setAlarmRinging(params)
        logRepository.saveAlarmLog(SaveAlarmLogParam.Ringing(alarmId = params.alarmId))
        return Response.Success(alarm)
    }
}