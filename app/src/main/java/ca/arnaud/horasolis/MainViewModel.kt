package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.GetRomanTimesParams
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.RomanTime
import ca.arnaud.horasolis.domain.RomanTimes
import ca.arnaud.horasolis.domain.SavedTimeScheduleParams
import ca.arnaud.horasolis.domain.SavedTimeScheduleUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
    private val savedTimeSchedule: SavedTimeScheduleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenModel>(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    private var currentRomanTimes: RomanTimes? = null

    init {
        viewModelScope.launch {
            val selectedCity = state.value.selectedCity
            refreshTimes(selectedCity)
        }
    }

    private suspend fun refreshTimes(selectedCity: City) {
        val params = GetRomanTimesParams(
            lat = selectedCity.latitude,
            lng = selectedCity.longitude,
            timZoneId = selectedCity.timeZone,
            date = LocalDate.now(),
        )
        val romanTimes = getRomanTimes(params).getDataOrNull() ?: return
        currentRomanTimes = romanTimes
        _state.update { model ->
            model.copy(
                selectedCity = selectedCity,
                times = romanTimes.toTimeItems(),
            )
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            refreshTimes(city)
        }
    }

    fun onTimeChecked(timeItem: TimeItem, checked: Boolean) {
        _state.update { model ->
            val updatedSelectedTimes = if (checked) {
                model.selectedTimes + timeItem
            } else {
                model.selectedTimes - timeItem
            }
            model.copy(selectedTimes = updatedSelectedTimes.toImmutableList())
        }
    }

    fun onSaveClicked() {
        val romanTimes = currentRomanTimes ?: return
        val selectedTimes = state.value.selectedTimes.mapNotNull { timeItem ->
            romanTimes.times.find { it.number == timeItem.number }
        }
        if (selectedTimes.isEmpty()) return
        viewModelScope.launch {
            val params = SavedTimeScheduleParams(
                lat = romanTimes.lat,
                lng = romanTimes.lng,
                timZoneId = romanTimes.timZoneId,
                times = selectedTimes,
            )
            savedTimeSchedule(params)
        }
    }

    private fun RomanTimes.toTimeItems(): ImmutableList<TimeItem> {
        return times.mapIndexed { index, time ->
            TimeItem(
                number = time.number,
                label = "Time ${time.number}",
                hour = time.startTime.formatTime(),
                night = time.type == RomanTime.Type.Night,
            )
        }.toImmutableList()
    }

    private fun LocalTime.formatTime(): String {
        return this.toString().split(".").first()
    }
}