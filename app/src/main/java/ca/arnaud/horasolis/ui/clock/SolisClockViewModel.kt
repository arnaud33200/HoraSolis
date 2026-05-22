package ca.arnaud.horasolis.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.location.GetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SolisClockData(
    val alarms: List<SavedAlarm>,
    val locations: List<SavedLocation>,
)

sealed interface SolisClockViewModelParams {

    /**
     * Allow updating user settings (e.g. current location)
     */
    data object Default : SolisClockViewModelParams

    /**
     * Only for preview, user settings won't be updated.
     * Used to explore different date and location combinations.
     */
    data object ViewOnly : SolisClockViewModelParams
}

class SolisClockViewModel(
    private val params: SolisClockViewModelParams,
    observeAllLocations: ObserveAllLocationsUseCase,
    observeSavedAlarms: ObserveSavedAlarmsUseCase,
    getCurrentLocation: GetCurrentLocationUseCase,
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val modelFactory: SolisClockWithTimeModelFactory,
    private val setCurrentLocation: SetCurrentLocationUseCase,
) : ViewModel() {

    private var solisClockData: SolisClockData = SolisClockData(
        alarms = emptyList(),
        locations = emptyList(),
    )

    private val _selectedDate = MutableStateFlow(timeProvider.getNowDate())

    /**
     * Selected location from the dropdown, initialized with the current location.
     * Used instead of observeCurrentLocation because current location is not update in view mode.
     */
    private val _selectedLocation = MutableStateFlow<SavedLocation?>(null)

    private val _state = MutableStateFlow<SolisClockWithTimeModel>(SolisClockWithTimeModel.Loading)
    val state: StateFlow<SolisClockWithTimeModel> = _state

    private var clockTickJob: Job? = null

    init {
        viewModelScope.launch {
            _selectedLocation.value = getCurrentLocation()
            combine(
                _selectedDate,
                _selectedLocation,
                observeAllLocations(),
                observeSavedAlarms(),
            ) { _, _, locations, alarms ->
                SolisClockData(
                    locations = locations,
                    alarms = alarms,
                )
            }.collectLatest { data ->
                refresh(data)
            }
        }
    }

    private suspend fun refresh(data: SolisClockData) {
        solisClockData = data
        val response = getSolisDay(
            atDate = _selectedDate.value, atLocation = _selectedLocation.value,
        )
        _state.value = modelFactory.create(response, data, params)

        clockTickJob?.cancel()
        clockTickJob = viewModelScope.launch {
            while (isActive) {
                _state.value = modelFactory.create(response, data, params)
                delay(response.solisSecondDelayMs())
            }
        }
    }

    fun onDateChanged(date: LocalDate) {
        val current = _state.value
        if (current is SolisClockWithTimeModel.Content) {
            _state.value = current.copy(isDateLoading = true)
        }
        _selectedDate.value = date
    }

    fun onLocationSelected(id: String) {
        val location = solisClockData.locations.find { it.id == id } ?: return
        val current = _state.value
        if (current is SolisClockWithTimeModel.Content) {
            _state.value = current.copy(isLocationLoading = true)
        }
        viewModelScope.launch {
            when (params) {
                SolisClockViewModelParams.Default -> setCurrentLocation(id)
                SolisClockViewModelParams.ViewOnly -> Unit // No-op, keep user settings
            }
            _selectedLocation.value = location
        }
    }

    private fun Response<SolisDay, GetSolisDayError>.solisSecondDelayMs(): Long {
        val data = getDataOrNull() ?: return 250L
        val solisTime = timeProvider.getNowSolisTime(data)
        val secondDuration = when (solisTime.type) {
            SolisTime.Type.Day -> data.solisDaySecondDuration
            SolisTime.Type.Night -> data.solisNightSecondDuration
        }
        return secondDuration.toMillis().coerceAtLeast(100L)
    }
}
