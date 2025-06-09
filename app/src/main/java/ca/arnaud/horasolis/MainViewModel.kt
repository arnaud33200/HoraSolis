package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.GetRomanTimesParams
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.RomanTimes
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
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenModel>(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

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
            date = LocalDate.now()
        )
        val romanTimes = getRomanTimes(params).getDataOrNull() ?: return
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

    private fun RomanTimes.toTimeItems(): ImmutableList<TimeItem> {
        return (dayTimes.mapIndexed { index, time ->
            TimeItem(
                label = "Day Time ${index + 1}",
                hour = time.formatTime(),
                night = false,
            )
        } + nightTimes.mapIndexed { index, time ->
            TimeItem(
                label = "Night Time ${index + 1}",
                hour = time.formatTime(),
                night = true,
            )
        }).toImmutableList()
    }

    private fun LocalTime.formatTime(): String {
        return this.toString().split(".").first()
    }
}