package ca.arnaud.horasolis.ui.solisviewer

import androidx.lifecycle.ViewModel
import ca.arnaud.horasolis.domain.provider.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class SolisViewerViewModel(
    private val timeProvider: TimeProvider,
    private val screenModelFactory: SolisViewerScreenModelFactory,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(timeProvider.getNowDate())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _state = MutableStateFlow(screenModelFactory.create(_selectedDate.value))
    val state: StateFlow<SolisViewerScreenModel> = _state

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    fun onAction(action: SolisViewerUserAction) {
        when (action) {
            SolisViewerUserAction.PreviousMonth -> updateDate(_selectedDate.value.minusMonths(1))
            SolisViewerUserAction.PreviousDay -> updateDate(_selectedDate.value.minusDays(1))
            SolisViewerUserAction.NextDay -> updateDate(_selectedDate.value.plusDays(1))
            SolisViewerUserAction.NextMonth -> updateDate(_selectedDate.value.plusMonths(1))
            SolisViewerUserAction.SelectDate -> _showDatePicker.value = true
            SolisViewerUserAction.NowClick -> updateDate(timeProvider.getNowDate())
        }
    }

    fun onDateSelected(date: LocalDate) {
        updateDate(date)
        _showDatePicker.value = false
    }

    fun onDismissDatePicker() {
        _showDatePicker.value = false
    }

    private fun updateDate(date: LocalDate) {
        _selectedDate.value = date
        _state.value = screenModelFactory.create(date)
    }
}
