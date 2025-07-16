package ca.arnaud.horasolis.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.ui.common.HoraAlertDialogModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val observeAlarmRinging: ObserveAlarmRingingUseCase,
    private val setAlarmRinging: SetAlarmRingingUseCase,
    private val horaAlertDialogFactory: HoraAlertDialogModelFactory,
) : ViewModel() {

    private val _ringingDialog = MutableStateFlow<HoraAlertDialogModel?>(null)
    val ringingDialog: StateFlow<HoraAlertDialogModel?> = _ringingDialog

    init {
        viewModelScope.launch {
            observeAlarmRinging().mapNotNull { it }.collectLatest { alarm ->
                _ringingDialog.value = horaAlertDialogFactory.create(alarm)
            }
        }
    }

    /**
     * Fallback when the service is already stopped but ringing state is still true.
     * This can happen if the service was stopped manually or due to an error.
     */
    fun onStopRingingServiceFailed() {
        viewModelScope.launch {
            setAlarmRinging(null)
        }
    }
}