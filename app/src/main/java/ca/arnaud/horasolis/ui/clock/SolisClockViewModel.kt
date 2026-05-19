package ca.arnaud.horasolis.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

class SolisClockViewModel(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val modelFactory: SolisClockDialogModelFactory,
    private val observeAllLocations: ObserveAllLocationsUseCase,
    private val setCurrentLocation: SetCurrentLocationUseCase,
) : ViewModel() {

    private var locations: List<SavedLocation> = emptyList()

    private val _selectedDate = MutableStateFlow(timeProvider.getNowDate())

    private val _state = MutableStateFlow<SolisClockWithTimeModel>(SolisClockWithTimeModel.Loading)
    val state: StateFlow<SolisClockWithTimeModel> = _state

    init {
        viewModelScope.launch {
            _selectedDate.collectLatest { date ->
                while (isActive) {
                    val response = getSolisDay(date)
                    _state.value = modelFactory.create(response, locations)
                    delay(response.solisSecondDelayMs())
                }
            }
        }
        viewModelScope.launch {
            observeAllLocations().collectLatest { newLocations ->
                locations = newLocations
                val current = _state.value
                if (current is SolisClockWithTimeModel.Content) {
                    _state.value = current.copy(
                        locations = newLocations.map {
                            LocationDropdownItem(id = it.id, name = it.name.ifBlank { it.id })
                        }.toImmutableList()
                    )
                }
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
