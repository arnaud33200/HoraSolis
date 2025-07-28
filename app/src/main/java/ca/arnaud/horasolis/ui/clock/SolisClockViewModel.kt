package ca.arnaud.horasolis.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SolisClockViewModel(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val modelFactory: SolisClockDialogModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<SolisClockDialogModel>(
        SolisClockDialogModel.Loading,
    )
    val state: StateFlow<SolisClockDialogModel> = _state

    init {
        refreshClock()
    }

    fun refreshClock() {
        viewModelScope.launch {
            val response = getSolisDay(timeProvider.getNowDate())
            _state.value = modelFactory.create(response)
        }
    }
}

