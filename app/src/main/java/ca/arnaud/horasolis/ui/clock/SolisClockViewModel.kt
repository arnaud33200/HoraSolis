package ca.arnaud.horasolis.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import kotlinx.coroutines.delay
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
        viewModelScope.launch {
            while (true) {
                val response = getSolisDay(timeProvider.getNowDate())
                _state.value = modelFactory.create(response)
                delay(response.solisSecondDelayMs())
            }
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

