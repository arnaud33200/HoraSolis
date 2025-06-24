package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.GetRomanTimesParams
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.RomanTimes
import ca.arnaud.horasolis.domain.SavedTimeScheduleParams
import ca.arnaud.horasolis.domain.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.domain.SetAlarmRingingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
    private val savedTimeSchedule: SavedTimeScheduleUseCase,
    private val observeSelectedTimes: ObserveSelectedTimesUseCase,
    private val observeAlarmRinging: ObserveAlarmRingingUseCase,
    private val setAlarmRinging: SetAlarmRingingUseCase,
    private val screenModelFactory: MainScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    // TODO - setup a model so we can tell which alarm is ringing
    private val _ringingDialog = MutableStateFlow(false)
    val ringingDialog: StateFlow<Boolean> = _ringingDialog

    private var currentRomanTimes: RomanTimes? = null
    private var selectedTimeNumbers: Set<Int> = emptySet()

    init {
        viewModelScope.launch {
            val selectedCity = state.value.selectedCity
            refreshTimes(selectedCity)

            observeSelectedTimes().collectLatest { selectedTimes ->
                selectedTimeNumbers = selectedTimes.map { it.number }.toSet()
                _state.update { model ->
                    screenModelFactory.updateCheckedTimes(model, selectedTimeNumbers)
                }
            }
        }

        viewModelScope.launch {
            observeAlarmRinging().collectLatest { ringing ->
                _ringingDialog.value = ringing
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
            screenModelFactory.updateTimes(
                romanTimes.times,
                selectedTimeNumbers,
                model.copy(selectedCity = selectedCity)
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
        _state.update { model ->
            screenModelFactory.updateCheckedTimes(model, selectedTimeNumbers)
        }
    }

    fun onSaveClicked() {
        val romanTimes = currentRomanTimes ?: return
        val selectedTimes = state.value.times
            .filter { selectedTimeNumbers.contains(it.number) }
            .mapNotNull { timeItem ->
                romanTimes.times.find { it.number == timeItem.number }
            }
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

    /**
     * Fallback when the service is already stopped but ringing state is still true.
     * This can happen if the service was stopped manually or due to an error.
     */
    fun onStopRingingServiceFailed() {
        viewModelScope.launch {
            setAlarmRinging(false)
        }
    }
}