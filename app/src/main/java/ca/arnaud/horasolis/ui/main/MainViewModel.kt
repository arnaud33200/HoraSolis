package ca.arnaud.horasolis.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.AlarmRinging
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.ui.common.HoraAlertDialogModel
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val observeAlarmRinging: ObserveAlarmRingingUseCase,
    private val setAlarmRinging: SetAlarmRingingUseCase,
    private val stringProvider: StringProvider, // TODO - use a proper factory
) : ViewModel() {

    private val _ringingDialog = MutableStateFlow<HoraAlertDialogModel?>(null)
    val ringingDialog: StateFlow<HoraAlertDialogModel?> = _ringingDialog

    init {
        viewModelScope.launch {
            observeAlarmRinging().collectLatest { alarmRinging ->
                _ringingDialog.value = createRingingDialog(alarmRinging)
            }
        }
    }

    private fun createRingingDialog(alarmRinging: AlarmRinging?): HoraAlertDialogModel? {
        if (alarmRinging == null) return null
        return HoraAlertDialogModel(
            title = stringProvider.getString(
                R.string.ringing_alarm_dialog_title,
                alarmRinging.number.toString(),
            ),
            message = stringProvider.getString(R.string.ringing_alarm_dialog_message),
        )
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