package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import io.ktor.util.date.WeekDay

data object GetAlarmError

sealed interface GetAlarmParams {

    data object New : GetAlarmParams

    data class Existing(
        val alarmId: Int,
    ) : GetAlarmParams
}

class GetAlarmUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(
        params: GetAlarmParams,
    ): Response<Alarm, GetAlarmError> {
        return when (params) {
            is GetAlarmParams.New -> Response.Success(
                Alarm.empty.copy(
                    solisTime = SolisTime(
                        3, 30, 0, SolisTime.Type.Day
                    ),
                    onForWeekDays = WeekDay.entries.toSet(),
                )
            )

            is GetAlarmParams.Existing -> {
                alarmRepository.getAlarmOrNull(params.alarmId)?.let { alarm ->
                    Response.Success(alarm)
                } ?: Response.Failure(GetAlarmError)
            }
        }
    }
}