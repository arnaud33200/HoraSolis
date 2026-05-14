package ca.arnaud.horasolis.ui.alarmmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.alarm.NewAlarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.onFailure
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveLocationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmManagerViewModel(
    private val observeSavedAlarms: ObserveSavedAlarmsUseCase,
    private val upsertAlarm: UpsertAlarmUseCase,
    private val deleteAlarm: DeleteAlarmUseCase,
    private val getSolisDay: GetSolisDayUseCase,
    private val alarmListFactory: AlarmListModelFactory,
    private val observeLocation: ObserveLocationUseCase,
) : ViewModel() {

    private var currentAlarms: List<SavedAlarm> = emptyList()

    private val _state =
        MutableStateFlow<AlarmManagerScreenModel>(AlarmManagerScreenModel.Loading())
    val state: StateFlow<AlarmManagerScreenModel> = _state

    private val _timePickerDialogModel = MutableStateFlow<EditSolisAlarmDialogModel?>(null)
    val timePickerDialogModel: StateFlow<EditSolisAlarmDialogModel?> = _timePickerDialogModel

    private val _navigateToEditAlarm = MutableSharedFlow<Long?>()
    val navigateToEditAlarm: SharedFlow<Long?> = _navigateToEditAlarm

    private var solisDay: SolisDay? = null

    init {
        // TODO - setup a formater for location text fields
        viewModelScope.launch {
            observeCurrentLocation()
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
        if (solisDay == null) {
            _state.value = AlarmManagerScreenModel.MissingLocation()
            return
        }

        _state.update { model ->
            val list = alarmListFactory.create(
                savedAlarms = currentAlarms,
                solisDay = solisDay ?: getSolisDay(LocalDate.now()).getDataOrNull(),
            )
            (model as? AlarmManagerScreenModel.Content)?.copy(list = list)
                ?: AlarmManagerScreenModel.Content(list = list)
        }
    }

    private suspend fun observeCurrentLocation() {
        observeLocation().collectLatest { location ->
            if (location == null) {
                _state.value = AlarmManagerScreenModel.MissingLocation()
            } else {
                if (state.value !is AlarmManagerScreenModel.Content) {
                    _state.value = AlarmManagerScreenModel.Loading()
                } else {
                    // TODO - show loading on the alarm civil time
                }
                solisDay = getSolisDay(LocalDate.now()).getDataOrNull()
                refreshAlarmList()
            }
        }
    }

    fun onAddClick() {
        viewModelScope.launch { _navigateToEditAlarm.emit(null) }
    }

    fun onTimePicked(params: EditSolisAlarmDialogModel) {
        _timePickerDialogModel.value = null
        viewModelScope.launch {
            val solisTime = params.getSolisTime()
            val label = "" // TODO - setup a text field
            val onForWeekDays = params.dayOfWeeks.mapNotNull { item ->
                item.data.takeIf { item.selected }
            }.toSet()
            val alarm = if (params.id != null) {
                SavedAlarm(
                    id = params.id,
                    label = label,
                    solisTime = params.getSolisTime(),
                    enabled = true,
                    onForWeekDays = onForWeekDays,
                )
            } else {
                NewAlarm(
                    label = label,
                    solisTime = solisTime,
                    enabled = true,
                    onForWeekDays = onForWeekDays,
                )
            }
            upsertAlarm(alarm)
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

    fun onAlarmItemClick(item: AlarmItemModel) {
        viewModelScope.launch { _navigateToEditAlarm.emit(item.id.toLong()) }
    }

    fun onAlarmToggleClick(
        item: AlarmItemModel,
        isEnabled: Boolean,
    ) {
        val alarm = currentAlarms.firstOrNull { it.id == item.id } ?: return
        viewModelScope.launch {
            val updatedAlarm = SavedAlarm(
                id = alarm.id,
                label = alarm.label,
                solisTime = alarm.solisTime,
                enabled = isEnabled,
                onForWeekDays = alarm.onForWeekDays,
            )
            upsertAlarm(updatedAlarm).onFailure {
                // TODO - show error
            }
        }
    }
}