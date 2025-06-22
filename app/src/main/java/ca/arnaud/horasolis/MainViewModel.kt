package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.GetRomanTimesParams
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.RomanTime
import ca.arnaud.horasolis.domain.RomanTimes
import ca.arnaud.horasolis.domain.SavedTimeScheduleParams
import ca.arnaud.horasolis.domain.SavedTimeScheduleUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
    private val savedTimeSchedule: SavedTimeScheduleUseCase,
    private val observeSelectedTimes: ObserveSelectedTimesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenModel>(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    private var currentRomanTimes: RomanTimes? = null
    private var selectedTimeNumbers: Set<Int> = emptySet()

    init {
        viewModelScope.launch {
            val selectedCity = state.value.selectedCity
            refreshTimes(selectedCity)

            observeSelectedTimes().collectLatest { selectedTimes ->
                selectedTimeNumbers = selectedTimes.map { it.number }.toSet()
                updateCheckedTimes(selectedTimeNumbers)
            }
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
                times = romanTimes.times.toTimeItems(),
            )
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            refreshTimes(city)
        }
    }

    fun onTimeChecked(timeItem: TimeItem, checked: Boolean) {
        if (checked) {
            selectedTimeNumbers = selectedTimeNumbers + timeItem.number
        } else {
            selectedTimeNumbers = selectedTimeNumbers - timeItem.number
        }
        updateCheckedTimes(selectedTimeNumbers)
    }

    fun onSaveClicked() {
        val romanTimes = currentRomanTimes ?: return
        val selectedTimes = state.value.times.filter { selectedTimeNumbers.contains(it.number) }.mapNotNull { timeItem ->
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

    private fun updateCheckedTimes(selectedNumbers: Set<Int>) {
        _state.update { model ->
            val updatedTimes = model.times.map { timeItem ->
                timeItem.copy(checked = selectedNumbers.contains(timeItem.number))
            }.toImmutableList()
            model.copy(times = updatedTimes)
        }
    }

    private fun List<RomanTime>.toTimeItems(): ImmutableList<TimeItem> {
        return this.map { time ->
            TimeItem(
                number = time.number,
                label = "Time ${time.number}",
                hour = time.startTime.formatTime(),
                night = time.type == RomanTime.Type.Night,
                checked = selectedTimeNumbers.contains(time.number),
            )
        }.toImmutableList()
    }

    private fun LocalTime.formatTime(): String {
        return this.toString().split(".").first()
    }
}