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

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenModel>(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    init {
        viewModelScope.launch {
            refreshTimes()
        }
    }

    private suspend fun refreshTimes() {
        val selectedCity = state.value.selectedCity
        val params = GetRomanTimesParams(
            lat = selectedCity.latitude,
            lng = selectedCity.longitude,
            timZoneId = selectedCity.timeZone,
            date = LocalDate.now()
        )
        val romanTimes = getRomanTimes(params).getDataOrNull() ?: return
        _state.update { model ->
            model.copy(
                times = romanTimes.toTimeItems(),
            )
        }
    }

    fun onCitySelected(city: City) {
        _state.value = _state.value.copy(selectedCity = city)
    }

    fun onUpdateClicked() {
        viewModelScope.launch {
            refreshTimes()
        }
    }

    private fun RomanTimes.toTimeItems(): ImmutableList<TimeItem> {
        return (dayTimes.mapIndexed { index, dateTime ->
            TimeItem(
                label = "Day Time ${index + 1}",
                hour = dateTime.toString(),
                night = false,
            )
        } + nightTimes.mapIndexed { index, dateTime ->
            TimeItem(
                label = "Night Time ${index + 1}",
                hour = dateTime.toString(),
                night = true,
            )
        }).toImmutableList()
    }
}