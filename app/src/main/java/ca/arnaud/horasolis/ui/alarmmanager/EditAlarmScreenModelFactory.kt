package ca.arnaud.horasolis.ui.alarmmanager

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.Alarm.Schedule
import ca.arnaud.horasolis.domain.model.alarm.AlarmUpdateParams
import ca.arnaud.horasolis.domain.model.alarm.applyUpdates
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.solis.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.GetSettingsUseCase
import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.common.RingtoneProvider
import ca.arnaud.horasolis.ui.common.StringProvider
import ca.arnaud.horasolis.ui.common.WheelPickerItem
import ca.arnaud.horasolis.ui.editalarm.EditAlarmScreenModel
import ca.arnaud.horasolis.ui.editalarm.ScheduleContent
import java.time.DayOfWeek
import kotlinx.collections.immutable.toImmutableList

class EditAlarmScreenModelFactory(
    private val getSolisDay: GetSolisDayUseCase,
    private val stringProvider: StringProvider,
    private val timeProvider: TimeProvider,
    private val dateFormatter: DateFormatter,
    private val ringtoneProvider: RingtoneProvider,
    private val getSettings: GetSettingsUseCase,
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
        val scheduleContent = when (val schedule = updatedAlarm.schedule) {
            is Schedule.Repeating -> {
                val dayOfWeeks = DayOfWeek.entries.map { dayOfWeek ->
                    EditDayOfWeekItemModel(
                        text = dateFormatter.formatWeekDay(dayOfWeek),
                        selected = schedule.weekDays.contains(dayOfWeek),
                        data = dayOfWeek,
                    )
                }.toImmutableList()
                ScheduleContent.Repeating(dayOfWeeks)
            }

            is Schedule.OneTime -> ScheduleContent.OneTime(
                selectedDate = dateFormatter.formatDate(schedule.date),
            )
        }
        val settings = getSettings()
        val effectiveSoundUri = updatedAlarm.soundUri ?: settings.ringtoneUrl
        val soundName = ringtoneProvider.getNameOrNull(effectiveSoundUri)
            ?: stringProvider.getString(R.string.alarm_sound_default)

        val hour = updatedAlarm.solisTime.hour
        val minute = updatedAlarm.solisTime.minute
        val hourItems = (0..11).map { WheelPickerItem(text = it.toString(), data = it + 1) }.toImmutableList()
        val minuteItems = (0..59).map { WheelPickerItem(text = it.toString().padStart(2, '0'), data = it) }.toImmutableList()
        val selectedHourItem = hourItems.firstOrNull { it.data == hour } ?: hourItems[0]
        val selectedMinuteItem = minuteItems.firstOrNull { it.data == minute } ?: minuteItems[0]
        return EditAlarmScreenModel.Content(
            hourTitle = stringProvider.getString(
                R.string.hour_label, "${hour - 1}",
            ),
            hourItems = hourItems,
            selectedHourItem = selectedHourItem,
            minuteTitle = stringProvider.getString(
                R.string.minute_label, "$minute",
            ),
            minuteItems = minuteItems,
            selectedMinuteItem = selectedMinuteItem,
            isDay = updatedAlarm.solisTime.type == SolisTime.Type.Day,
            civilTime = civilTime.orEmpty(),
            scheduleContent = scheduleContent,
            soundName = soundName,
            vibrationEnabled = updatedAlarm.vibrate ?: settings.vibrate,
            saveEnabled = true,
        )
    }

    private suspend fun getSolisDayOrNull(): SolisDay? {
        return solisDay ?: getSolisDay(timeProvider.getNowDate()).getDataOrNull()?.also {
            solisDay = it
        }
    }
}
