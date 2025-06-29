package ca.arnaud.horasolis.ui.main

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.AlarmRinging
import ca.arnaud.horasolis.domain.usecase.RomanTimes
import ca.arnaud.horasolis.ui.common.AlertDialogModel
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainScreenModelFactory(
    private val timeProvider: TimeProvider,
    private val stringProvider: StringProvider,
) {

    fun createInitialLoading(): MainScreenModel {
        return MainScreenModel(
            loading = MainScreenModel.Loading.Content,
            dayTimes = TimeListModel(
                times = List(12) {
                    TimeItem(
                        number = it + 1,
                        label = stringProvider.getString(
                            R.string.time_item_day_label,
                            (it + 1).toString()
                        ),
                        hour = "00:00",
                        night = false,
                    )
                }.toImmutableList()
            ),
            nightTimes = TimeListModel(
                times = List(12) {
                    TimeItem(
                        number = it + 13,
                        label = stringProvider.getString(
                            R.string.time_item_night_label,
                            (it + 1).toString()
                        ),
                        hour = "00:00",
                        night = true,
                    )
                }.toImmutableList(),
            ),
        )
    }

    fun updateTimes(
        romanTimes: RomanTimes,
        settings: ScheduleSettings?,
        model: MainScreenModel
    ): MainScreenModel {
        return model.copy(
            dayTimes = romanTimes.toTimeListModel(settings, RomanTime.Type.Day),
            nightTimes = romanTimes.toTimeListModel(settings, RomanTime.Type.Night),
        )
    }

    fun updateSelectedTimes(
        model: MainScreenModel,
        timeItem: TimeItem,
        checked: Boolean,
    ): MainScreenModel {
        return model.copy(
            dayTimes = model.dayTimes.updateChecked(timeItem.number, checked),
            nightTimes = model.nightTimes.updateChecked(timeItem.number, checked),
        )
    }

    private fun TimeListModel.updateChecked(
        number: Int,
        checked: Boolean,
    ): TimeListModel {
        return this.copy(
            times = this.times.map { time ->
                if (time.number == number) {
                    time.copy(checked = checked)
                } else {
                    time
                }
            }.toImmutableList()
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
            dayTimes = model.dayTimes.copy(
                times = model.dayTimes.times.map {
                    it.copy(checked = selectedTimeNumbers.contains(it.number))
                }.toImmutableList()
            ),
            nightTimes = model.nightTimes.copy(
                times = model.nightTimes.times.map {
                    it.copy(checked = selectedTimeNumbers.contains(it.number))
                }.toImmutableList()
            ),
        )
    }

    private fun ScheduleSettings?.toSelectedTimeNumbers(): Set<Int> {
        return this?.selectedTime?.map { it.number }?.toSet() ?: emptySet()
    }

    private fun RomanTimes.toTimeListModel(
        settings: ScheduleSettings?,
        forType: RomanTime.Type,
    ): TimeListModel {
        val timeItems = times.toTimeItems(settings, forType)
        val description = when (forType) {
            RomanTime.Type.Day -> dayDuration.formatToHours()
            RomanTime.Type.Night -> nightDuration.formatToHours()
        }
        return TimeListModel(
            description = description,
            times = timeItems,
        )
    }

    private fun List<RomanTime>.toTimeItems(
        settings: ScheduleSettings?,
        forType: RomanTime.Type,
    ): ImmutableList<TimeItem> {
        val nowDateTime = timeProvider.getNowDateTime()
        val selectedTimeNumbers = settings.toSelectedTimeNumbers()
        return this.filter { it.type == forType }.map { time ->
            val startDateTime = time.startTime.atDate(nowDateTime.toLocalDate())
            val endDateTime = time.endTime.atDate(nowDateTime.toLocalDate())
            val isNow = nowDateTime.isAfter(startDateTime) && nowDateTime.isBefore(endDateTime)
            val label = if (time.type == RomanTime.Type.Night) {
                stringProvider.getString(
                    R.string.time_item_night_label,
                    (time.number - 12).toString()
                )
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

    private fun Duration.formatToHours(): String {
        val hours = this.toHours()
        val minutes = this.minusHours(hours).toMinutes()
        return String.format("%02d:%02d", hours, minutes)
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
