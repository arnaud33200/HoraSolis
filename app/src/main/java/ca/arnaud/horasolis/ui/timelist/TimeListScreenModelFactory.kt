package ca.arnaud.horasolis.ui.timelist

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.alarm.AlarmRinging
import ca.arnaud.horasolis.domain.usecase.RomanTimes
import ca.arnaud.horasolis.ui.common.HoraAlertDialogModel
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeListScreenModelFactory(
    private val timeProvider: TimeProvider,
    private val stringProvider: StringProvider,
) {

    fun createInitialLoading(): TimeListScreenModel {
        return TimeListScreenModel(
            loading = TimeListScreenModel.Loading.Content,
            dayTimes = TimeListModel(
                times = List(12) {
                    TimeItem(
                        number = it + 1,
                        label = stringProvider.getString(
                            R.string.time_item_day_label,
                            (it + 1).toString()
                        ),
                        hour = "00:00",
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
                    )
                }.toImmutableList(),
            ),
        )
    }

    fun updateTimes(
        romanTimes: RomanTimes,
        settings: ScheduleSettings?,
        model: TimeListScreenModel
    ): TimeListScreenModel {
        return model.copy(
            dayTimes = romanTimes.toTimeListModel(settings, RomanTime.Type.Day),
            nightTimes = romanTimes.toTimeListModel(settings, RomanTime.Type.Night),
        )
    }

    fun updateNowTime(
        model: TimeListScreenModel,
        romanTimes: RomanTimes?,
    ): TimeListScreenModel {
        val nowTime = timeProvider.getNowDateTime().toLocalTime()
        val nowRomanTime = romanTimes?.times?.find { it.isNow(nowTime) } ?: return model

        val transformItem: (TimeItem) -> TimeItem = { item ->
            item.copy(highlight = item.number == nowRomanTime.number)
        }
        return model.copy(
            dayTimes = model.dayTimes.copy(
                times = model.dayTimes.times.map(transformItem).toImmutableList()
            ),
            nightTimes = model.nightTimes.copy(
                times = model.nightTimes.times.map(transformItem).toImmutableList()
            ),
        )
    }

    fun updateSelectedTimes(
        model: TimeListScreenModel,
        timeItem: TimeItem,
        checked: Boolean,
    ): TimeListScreenModel {
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
        model: TimeListScreenModel,
        settings: ScheduleSettings?,
    ): TimeListScreenModel {
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
        val nowTime = timeProvider.getNowDateTime().toLocalTime()
        val selectedTimeNumbers = settings.toSelectedTimeNumbers()
        return this.filter { it.type == forType }.map { time ->
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
                checked = selectedTimeNumbers.contains(time.number),
                highlight = time.isNow(nowTime),
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
        model: TimeListScreenModel,
    ): TimeListScreenModel {
        return model.copy(
            loading = null,
            snackMessage = stringProvider.getString(R.string.settings_saved_message),
        )
    }
}