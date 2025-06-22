package ca.arnaud.horasolis

import ca.arnaud.horasolis.domain.RomanTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalTime

class MainScreenModelFactory {

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
            times = model.times.map { it.copy(checked = selectedTimeNumbers.contains(it.number)) }.toImmutableList()
        )
    }
}

private fun List<RomanTime>.toTimeItems(selectedNumbers: Set<Int>): ImmutableList<TimeItem> {
    return this.map { time ->
        TimeItem(
            number = time.number,
            label = "Time ${time.number}",
            hour = time.startTime.formatTime(),
            night = time.type == RomanTime.Type.Night,
            checked = selectedNumbers.contains(time.number),
        )
    }.toImmutableList()
}

private fun LocalTime.formatTime(): String {
    return this.toString().split(".").first()
}
