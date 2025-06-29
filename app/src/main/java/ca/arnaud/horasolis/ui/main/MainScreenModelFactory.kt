package ca.arnaud.horasolis.ui.main

import ca.arnaud.horasolis.ui.main.City
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.AlarmRinging
import ca.arnaud.horasolis.ui.common.AlertDialogModel
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainScreenModelFactory(
    private val timeProvider: TimeProvider,
    private val stringProvider: StringProvider,
) {

    fun updateTimes(
        times: List<RomanTime>,
        settings: ScheduleSettings?,
        model: MainScreenModel
    ): MainScreenModel {
        return model.copy(
            times = times.toTimeItems(settings)
        )
    }

    fun updateSelectedTimes(
        model: MainScreenModel,
        timeItem: TimeItem,
        checked: Boolean,
    ): MainScreenModel {

        val updatedTimes = model.times.map { time ->
            if (time.number == timeItem.number) {
                time.copy(checked = checked)
            } else {
                time
            }
        }
        return model.copy(
            times = updatedTimes.toImmutableList(),
        )
    }

    fun updateWithSettings(
        model: MainScreenModel,
        settings: ScheduleSettings?,
    ): MainScreenModel {
        val selectedTimeNumbers = settings.toSelectedTimeNumbers()
        val selectedCity = City.Companion.firstOrNull(settings?.location)

        return model.copy(
            selectedCity = selectedCity ?: City.entries.first(),
            times = model.times.map {
                it.copy(checked = selectedTimeNumbers.contains(it.number))
            }.toImmutableList()
        )
    }

    private fun ScheduleSettings?.toSelectedTimeNumbers(): Set<Int> {
        return this?.selectedTime?.map { it.number }?.toSet() ?: emptySet()
    }

    private fun List<RomanTime>.toTimeItems(
        settings: ScheduleSettings?,
    ): ImmutableList<TimeItem> {
        val nowDateTime = timeProvider.getNowDateTime()
        val selectedTimeNumbers = settings.toSelectedTimeNumbers()
        return this.map { time ->
            val startDateTime = time.startTime.atDate(nowDateTime.toLocalDate())
            val endDateTime = time.endTime.atDate(nowDateTime.toLocalDate())
            val isNow = nowDateTime.isAfter(startDateTime) && nowDateTime.isBefore(endDateTime)
            val label = if (time.type == RomanTime.Type.Night) {
                stringProvider.getString(R.string.time_item_night_label, (time.number - 12).toString())
            } else {
                stringProvider.getString(R.string.time_item_day_label, time.number.toString())
            }
            TimeItem(
                number = time.number,
                label = label,
                hour = time.startTime.formatTimeHHmm(),
                night = time.type == RomanTime.Type.Night,
                checked = selectedTimeNumbers.contains(time.number),
                highlight = isNow,
            )
        }.toImmutableList()
    }

    private fun LocalTime.formatTimeHHmm(): String {
        return DateTimeFormatter.ofPattern("HH:mm").format(this)
    }

    fun updateSavedSettings(
        model: MainScreenModel,
    ): MainScreenModel {
        return model.copy(
            loading = null,
            snackMessage = stringProvider.getString(R.string.settings_saved_message),
        )
    }

    fun createRingingDialog(alarmRinging: AlarmRinging?): AlertDialogModel? {
        return if (alarmRinging != null) {
            AlertDialogModel(
                title = stringProvider.getString(
                    R.string.ringing_alarm_dialog_title,
                    alarmRinging.number.toString(),
                ),
                message = stringProvider.getString(R.string.ringing_alarm_dialog_message),
            )
        } else {
            null
        }
    }
}
