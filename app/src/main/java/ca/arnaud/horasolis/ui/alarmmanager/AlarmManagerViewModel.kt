package ca.arnaud.horasolis.ui.alarmmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.model.NewAlarm
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val editAlarmDialogFactory: EditSolisAlarmDialogModelFactory,
    private val observeLocation: ObserveLocationUseCase,
) : ViewModel() {

    private var currentAlarms: List<SavedAlarm> = emptyList()

    private val _state = MutableStateFlow<AlarmManagerScreenModel>(AlarmManagerScreenModel.Content())
    val state: StateFlow<AlarmManagerScreenModel> = _state

    private val _timePickerDialogModel = MutableStateFlow<EditSolisAlarmDialogModel?>(null)
    val timePickerDialogModel: StateFlow<EditSolisAlarmDialogModel?> = _timePickerDialogModel

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
                // TODO - show loading on the alarm civil time
                solisDay = getSolisDay(LocalDate.now()).getDataOrNull()
                refreshAlarmList()
            }
        }
    }

    fun onAddClick() {
        _timePickerDialogModel.value = editAlarmDialogFactory.createNewAlarm(solisDay)
    }

    fun onTimePicked(params: EditSolisAlarmDialogModel) {
        _timePickerDialogModel.value = null
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
        val alarm = currentAlarms.firstOrNull { it.id == item.id } ?: return
        _timePickerDialogModel.value = editAlarmDialogFactory.createEditAlarm(
            alarm = alarm,
            solisDay = solisDay,
        )
    }
}