package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.AlarmUpdateParams
import ca.arnaud.horasolis.domain.model.alarm.applyUpdates
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.editalarm.EditAlarmScreenModel
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.toImmutableList
import ca.arnaud.horasolis.domain.model.alarm.Alarm.Schedule

class EditAlarmScreenModelFactory(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val dateFormatter: DateFormatter,
) {

    private var solisDay: SolisDay? = null

    suspend fun create(
        alarm: Alarm,
        updateParams: AlarmUpdateParams,
    ): EditAlarmScreenModel {
        val updatedAlarm = alarm.applyUpdates(updateParams)
        val civilTime = getSolisDayOrNull()?.let { solisDay ->
            dateFormatter.formatCivilTime(updatedAlarm.solisTime.toCivilTime(solisDay))
        }
        val dayOfWeeks = WeekDay.entries.map { dayOfWeek ->
            EditDayOfWeekItemModel(
                text = dateFormatter.formatWeekDay(dayOfWeek),
                selected = (updatedAlarm.schedule as? Schedule.Repeating)?.weekDays?.contains(dayOfWeek) ?: false,
                data = dayOfWeek,
            )
        }.toImmutableList()
        return EditAlarmScreenModel.Content(
            hour = updatedAlarm.solisTime.hour,
            minute = updatedAlarm.solisTime.minute,
            isDay = updatedAlarm.solisTime.type == SolisTime.Type.Day,
            civilTime = civilTime.orEmpty(),
            dayOfWeeks = dayOfWeeks,
        )
    }

    private suspend fun getSolisDayOrNull(): SolisDay? {
        return solisDay ?: getSolisDay(timeProvider.getNowDate()).getDataOrNull()?.also {
            solisDay = it
        }
    }
}
