package ca.arnaud.horasolis.ui.solisviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.ui.clock.SolisClockWithTimeModel
import ca.arnaud.horasolis.ui.clock.SolisClockDialogModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SolisViewerViewModel(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val clockModelFactory: SolisClockDialogModelFactory,
    private val screenModelFactory: SolisViewerScreenModelFactory,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(timeProvider.getNowDate())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _state = MutableStateFlow(
        screenModelFactory.create(_selectedDate.value, SolisClockWithTimeModel.Loading)
    )
    val state: StateFlow<SolisViewerScreenModel> = _state

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    init {
        viewModelScope.launch {
            updateClock(_selectedDate.value)
        }
        _selectedDate.drop(1).debounce(50).onEach { date ->
            updateClock(date)
        }.launchIn(viewModelScope)
    }

    private suspend fun updateClock(date: LocalDate) {
        _state.update { model -> model.copy(isLoading = true) }
        val response = getSolisDay(date)
        _state.value = screenModelFactory.create(date, clockModelFactory.create(response))
    }

    fun onAction(action: SolisViewerUserAction) {
        when (action) {
            SolisViewerUserAction.PreviousMonth -> _selectedDate.value =
                _selectedDate.value.minusMonths(1)

            SolisViewerUserAction.PreviousDay -> _selectedDate.value =
                _selectedDate.value.minusDays(1)

            SolisViewerUserAction.NextDay -> _selectedDate.value = _selectedDate.value.plusDays(1)
            SolisViewerUserAction.NextMonth -> _selectedDate.value =
                _selectedDate.value.plusMonths(1)

            SolisViewerUserAction.SelectDate -> _showDatePicker.value = true
            SolisViewerUserAction.NowClick -> _selectedDate.value = timeProvider.getNowDate()
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        _showDatePicker.value = false
    }

    fun onDismissDatePicker() {
        _showDatePicker.value = false
    }
}
