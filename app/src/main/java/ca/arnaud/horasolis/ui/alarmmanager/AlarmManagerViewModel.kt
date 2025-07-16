package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.NewAlarm
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.location.GetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

class AlarmManagerViewModel(
    private val locationService: LocationService,
    private val observeSavedAlarms: ObserveSavedAlarmsUseCase,
    private val upsertAlarm: UpsertAlarmUseCase,
    private val deleteAlarm: DeleteAlarmUseCase,
    private val setCurrentLocation: SetCurrentLocationUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase, // TODO - setup observe instead
    private val getSolisDay: GetSolisDayUseCase,
    private val alarmListFactory: AlarmListModelFactory,
    private val editAlarmDialogFactory: EditSolisAlarmDialogModelFactory,
) : ViewModel() {

    private var currentAlarms: List<SavedAlarm> = emptyList()

    private val _state = MutableStateFlow(AlarmManagerScreenModel())
    val state: StateFlow<AlarmManagerScreenModel> = _state

    private val _timePickerDialogModel = MutableStateFlow<EditSolisAlarmDialogModel?>(null)
    val timePickerDialogModel: StateFlow<EditSolisAlarmDialogModel?> = _timePickerDialogModel

    private var solisDay: SolisDay? = null

    init {
        // TODO - setup a formater for location text fields
        viewModelScope.launch {
            getCurrentLocation()?.let { userLocation ->
                state.value.latitude.setText(userLocation.lat.toString())
                state.value.longitude.setText(userLocation.lng.toString())
            }
            observeLocationTextFields()
        }

        viewModelScope.launch {
            observeSavedAlarms().collectLatest { alarms ->
                currentAlarms = alarms
                refreshAlarmList()
            }
        }
    }

    private suspend fun refreshAlarmList() {
        val solisDay = solisDay
            ?: getSolisDay(LocalDate.now()).getDataOrNull()?.also {
                this.solisDay = it
            }
        _state.update { model ->
            model.copy(
                list = alarmListFactory.create(
                    savedAlarms = currentAlarms,
                    solisDay = solisDay ?: getSolisDay(LocalDate.now()).getDataOrNull(),
                )
            )
        }
    }

    private suspend fun observeLocationTextFields() {
        snapshotFlow {
            state.value.latitude.text to state.value.longitude.text
        }.drop(1).debounce(1.seconds).collectLatest { (latitude, longitude) ->
            val params = SetCurrentLocationParams(
                lat = latitude.toString().toDoubleOrNull() ?: 0.0,
                long = longitude.toString().toDoubleOrNull() ?: 0.0,
            )
            setCurrentLocation(params)
            solisDay = getSolisDay(LocalDate.now()).getDataOrNull()
            refreshAlarmList()
        }
    }

    fun onAddClick() {
        _timePickerDialogModel.value = editAlarmDialogFactory.createNewAlarm(solisDay)
    }

    fun onTimePicked(params: EditSolisAlarmDialogModel) {
        viewModelScope.launch {
            val solisTime = params.getSolisTime()
            val label = "" // TODO - setup a text field
            val alarm = if (params.id != null) {
                SavedAlarm(
                    id = params.id,
                    label = label,
                    solisTime = params.getSolisTime(),
                    enabled = true,
                )
            } else {
                NewAlarm(
                    label = label,
                    solisTime = solisTime,
                    enabled = true,
                )
            }
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

    fun onAlarmItemClick(item: AlarmItemModel) {
        val alarm = currentAlarms.firstOrNull { it.id == item.id } ?: return
        _timePickerDialogModel.value = editAlarmDialogFactory.createEditAlarm(
            alarm = alarm,
            solisDay = solisDay,
        )
    }
}