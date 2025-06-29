package ca.arnaud.horasolis

import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.usecase.RomanTimes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MainScreenModel(
    val message: String = "",
    val selectedCity: City = City.Thiviers,
    val times: ImmutableList<TimeItem> = persistentListOf(),
    val showSaveButton: Boolean = false,
    val loading: Loading? = null,
) {

    enum class Loading {
        Content, Saving,
    }

    fun getUpdatedScheduleSettings(
        romanTimes: RomanTimes?,
    ): ScheduleSettings? {
        if (romanTimes == null) return null
        val selectedTimes = times
            .filter { it.checked }
            .mapNotNull { timeItem ->
                romanTimes.times.find { it.number == timeItem.number }
            }
        val location = selectedCity.toUserLocation()
        return ScheduleSettings(
            location = location,
            selectedTime = selectedTimes,
        )
    }
}