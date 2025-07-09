package ca.arnaud.horasolis.ui.alarmmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.NewAlarm
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AlarmManagerViewModel(
    private val locationService: LocationService,
    private val observeSavedAlarms: ObserveSavedAlarmsUseCase,
    private val upsertAlarm: UpsertAlarmUseCase,
    private val deleteAlarm: DeleteAlarmUseCase,
    private val alarmListFactory: AlarmListModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow(AlarmManagerScreenModel())
    val state: StateFlow<AlarmManagerScreenModel> = _state

    private val _timePickerDialogModel = MutableStateFlow<EditSolisAlarmParams?>(null)
    val timePickerDialogModel: StateFlow<EditSolisAlarmParams?> = _timePickerDialogModel

    init {
        viewModelScope.launch {
            observeSavedAlarms().collectLatest { alarms ->
                _state.update { model ->
                    model.copy(
                        list = alarmListFactory.create(alarms)
                    )
                }
            }
        }
    }

    fun onAddClick() {
        // TODO - setup model to have picker on add or edit
        _timePickerDialogModel.value = EditSolisAlarmParams()
    }

    fun onTimePicked(params: EditSolisAlarmParams) {
        viewModelScope.launch {
            val alarm = NewAlarm(
                label = "",
                solisTime = params.getSolisTime(),
                enabled = true,
            )
            upsertAlarm(alarm)
            _timePickerDialogModel.value = null
        }
    }

    fun onDialogDismiss() {
        _timePickerDialogModel.value = null
    }

    fun onAlarmDeleteClick(item: AlarmItemModel) {
        viewModelScope.launch {
            deleteAlarm(item.id)
        }
    }

    fun onCurrentLocationClick(permissionResult: PermissionResult) {
        when (permissionResult) {
            PermissionResult.Granted -> viewModelScope.launch {
                updateCurrentLocation()
            }

            PermissionResult.Denied,
            PermissionResult.PermanentlyDenied -> {
                // TODO - show toast
            }
        }
    }

    private suspend fun updateCurrentLocation() {
        when (val response = locationService.getCurrentLocation()) {
            is Response.Success -> {
                state.value.latitude.setText(response.data.latitude.toString())
                state.value.longitude.setText(response.data.longitude.toString())
            }

            is Response.Failure -> {
                // TODO - show error
            }
        }
    }
}