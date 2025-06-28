package ca.arnaud.horasolis

import androidx.annotation.StringRes
import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.provider.TimeProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalTime

class MainScreenModelFactory(
    private val timeProvider: TimeProvider,
    private val stringProvider: StringProvider,
) {

    fun create(
        message: String,
        selectedCity: City,
        times: List<RomanTime>,
        selectedTimeNumbers: Set<Int>,
    ): MainScreenModel {
        return MainScreenModel(
            message = message,
            selectedCity = selectedCity,
            times = times.toTimeItems(selectedTimeNumbers),
        )
    }

    fun updateTimes(
        times: List<RomanTime>,
        selectedTimeNumbers: Set<Int>,
        model: MainScreenModel
    ): MainScreenModel {
        return model.copy(
            times = times.toTimeItems(selectedTimeNumbers)
        )
    }

    fun updateCheckedTimes(
        model: MainScreenModel,
        selectedTimeNumbers: Set<Int>
    ): MainScreenModel {
        return model.copy(
            times = model.times.map { it.copy(checked = selectedTimeNumbers.contains(it.number)) }
                .toImmutableList()
        )
    }


    private fun List<RomanTime>.toTimeItems(
        selectedNumbers: Set<Int>,
    ): ImmutableList<TimeItem> {
        val nowDateTime = timeProvider.getNowDateTime()
        return this.map { time ->
            val startDateTime = time.startTime.atDate(nowDateTime.toLocalDate())
            val endDateTime = time.endTime.atDate(nowDateTime.toLocalDate())
            val isNow = nowDateTime.isAfter(startDateTime) && nowDateTime.isBefore(endDateTime)
            @StringRes val labelRes = if (isNow) R.string.time_item_now_label else R.string.time_item_label
            val label = stringProvider.getString(labelRes, time.number.toString())
            TimeItem(
                number = time.number,
                label = label,
                hour = time.startTime.formatTime(),
                night = time.type == RomanTime.Type.Night,
                checked = selectedNumbers.contains(time.number),
                highlight = isNow,
            )
        }.toImmutableList()
    }

    private fun LocalTime.formatTime(): String {
        return this.toString().split(".").first()
    }
}
