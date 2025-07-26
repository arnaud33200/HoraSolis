package ca.arnaud.horasolis.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SolisClockViewModel(
    private val modelFactory: SolisClockDialogModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<SolisClockDialogModel>(
        SolisClockDialogModel.Loading,
    )
    val state: StateFlow<SolisClockDialogModel> = _state

    init {
        viewModelScope.launch {
            _state.value = modelFactory.create()
        }
    }
}

