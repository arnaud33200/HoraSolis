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
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

private data class SolisClockData(
    val alarms: List<SavedAlarm>,
    val locations: List<SavedLocation>,
)

class SolisClockViewModel(
    observeAllLocations: ObserveAllLocationsUseCase,
    observeSavedAlarms: ObserveSavedAlarmsUseCase,
    observeCurrentLocation: ObserveCurrentLocationUseCase,
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val modelFactory: SolisClockWithTimeModelFactory,
    private val setCurrentLocation: SetCurrentLocationUseCase,
) : ViewModel() {

    private var locations: List<SavedLocation> = emptyList()
    private var alarms: List<SavedAlarm> = emptyList()

    private val _selectedDate = MutableStateFlow(timeProvider.getNowDate())

    private val _state = MutableStateFlow<SolisClockWithTimeModel>(SolisClockWithTimeModel.Loading)
    val state: StateFlow<SolisClockWithTimeModel> = _state

    private var clockTickJob: Job? = null

    init {
        combine(
            _selectedDate,
            observeCurrentLocation(),
            observeAllLocations(),
            observeSavedAlarms(),
        ) { _, _, locations, alarms ->
            SolisClockData(
                locations = locations,
                alarms = alarms,
            )
        }.onEach { data ->
            locations = data.locations
            alarms = data.alarms
            refresh()
        }.launchIn(viewModelScope)
    }

    private suspend fun refresh() {
        val response = getSolisDay(_selectedDate.value)
        _state.value = modelFactory.create(response, locations, alarms)

        clockTickJob?.cancel()
        clockTickJob = viewModelScope.launch {
            while (isActive) {
                _state.value = modelFactory.create(response, locations, alarms)
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
        viewModelScope.launch {
            val current = _state.value
            if (current is SolisClockWithTimeModel.Content) {
                _state.value = current.copy(isLocationLoading = true)
            }
            setCurrentLocation(id)
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
