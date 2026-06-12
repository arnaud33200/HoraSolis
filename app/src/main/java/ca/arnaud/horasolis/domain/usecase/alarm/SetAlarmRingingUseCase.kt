package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.LogRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.onFailure
import ca.arnaud.horasolis.domain.provider.Logger

data class SetAlarmRingingParams(
    val alarmId: Int,
)

sealed interface SetAlarmRingingError {

    data object AlarmNotFound : SetAlarmRingingError
}

class SetAlarmRingingException(message: String) : Exception("SetAlarmRinging failed: $message")

class SetAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
    private val logRepository: LogRepository,
    private val logger: Logger,
) {

    suspend operator fun invoke(
        params: SetAlarmRingingParams,
    ): Response<SavedAlarm, SetAlarmRingingError> {
        return setAlarmRinging(params).onFailure { error ->
            logger.logNonFatal(SetAlarmRingingException(error::class.simpleName ?: error.toString()))
        }
    }

    private suspend fun setAlarmRinging(
        params: SetAlarmRingingParams,
    ): Response<SavedAlarm, SetAlarmRingingError> {
        val alarm = alarmRepository.getAlarmOrNull(params.alarmId)
            ?: return Response.Failure(SetAlarmRingingError.AlarmNotFound)
        alarmRepository.setAlarmRinging(params)
        logRepository.saveAlarmLog(SaveAlarmLogParam.Ringing(alarmId = params.alarmId))
        return Response.Success(alarm)
    }
}