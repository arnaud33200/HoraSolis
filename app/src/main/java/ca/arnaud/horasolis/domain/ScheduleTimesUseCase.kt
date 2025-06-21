package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.RomanTimeAlarmService
import java.time.LocalDate

data class ScheduleTimesParams(
    val times: List<RomanTime>,
)

class ScheduleTimesUseCase(
    private val alarmService: RomanTimeAlarmService,
) {

    operator fun invoke(params: ScheduleTimesParams) {
        params.times.forEach { time ->
            // TODO - figure out depending if the time already passed today or not
            val atDate = LocalDate.now()
            val alarmParams = RomanTimeAlarmScheduleParam(
                number = time.number,
                dateTime = atDate.atTime(time.startTime)
            )
            alarmService.scheduleAlarm(alarmParams)
        }
    }
}