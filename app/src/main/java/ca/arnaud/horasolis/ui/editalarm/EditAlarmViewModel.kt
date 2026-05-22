package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.Alarm.Schedule
import ca.arnaud.horasolis.domain.model.alarm.AlarmUpdateParams
import ca.arnaud.horasolis.domain.model.alarm.applyUpdates
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.usecase.alarm.GetAlarmParams
import ca.arnaud.horasolis.domain.usecase.alarm.GetAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.ui.alarmmanager.EditAlarmScreenModelFactory
import ca.arnaud.horasolis.ui.common.DatePickerModel
import ca.arnaud.horasolis.ui.common.DatePickerModelFactory
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

internal sealed interface EditAlarmViewModelParams {

    data object New : EditAlarmViewModelParams

    data class Edit(
        val alarmId: Int,
    ) : EditAlarmViewModelParams
}

sealed interface EditAlarmViewModelEvent {

    data object SaveSuccess : EditAlarmViewModelEvent

    data object NavigateBack : EditAlarmViewModelEvent
}

internal class EditAlarmViewModel(
    private val params: EditAlarmViewModelParams,
    private val getAlarm: GetAlarmUseCase,
    private val screenModelFactory: EditAlarmScreenModelFactory,
    private val upsertAlarm: UpsertAlarmUseCase,
    private val datePickerModelFactory: DatePickerModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<EditAlarmScreenModel>(EditAlarmScreenModel.Loading)
    val state: StateFlow<EditAlarmScreenModel> = _state

    private val _event = MutableSharedFlow<EditAlarmViewModelEvent>()
    val event: SharedFlow<EditAlarmViewModelEvent> = _event

    private val _datePickerModel = MutableStateFlow<DatePickerModel?>(null)
    val datePickerModel: StateFlow<DatePickerModel?> = _datePickerModel

    private val _showUnsavedChangesDialog = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog

    /**
     * text field state for the alarm label.
     * Shall be replaced with EditAlarmFieldState data class when more fields are added.
     */
    val labelState = TextFieldState()

    private var initialAlarm: Alarm = Alarm.empty
    private var updateParams: AlarmUpdateParams = AlarmUpdateParams()

    init {
        viewModelScope.launch {
            val getParams = when (params) {
                EditAlarmViewModelParams.New -> GetAlarmParams.New
                is EditAlarmViewModelParams.Edit -> GetAlarmParams.Existing(params.alarmId)
            }
            when (val response = getAlarm(getParams)) {
                is Response.Success -> {
                    initialAlarm = response.data
                    labelState.edit { append(initialAlarm.label.orEmpty()) }
                    rebuildState()
                    observeLabelChanges()
                }

                is Response.Failure -> {
                    // TODO - handle error state
                }
            }
        }
    }

    private suspend fun observeLabelChanges() {
        snapshotFlow { labelState.text }.collect { text ->
            val labelText = text.toString().takeIf { it.isNotBlank() }
            updateParams = updateParams.copy(
                label = UpdateParam.of(initialAlarm.label, labelText)
            )
            rebuildState()
        }
    }


    fun onAction(action: EditAlarmUiAction) {
        viewModelScope.launch {
            when (action) {
                is SolisTimeAction -> onSolisTimeChanged(action)
                is EditAlarmUiAction.DayOfWeekClicked -> onDayOfWeekClicked(action)
                is EditAlarmUiAction.ScheduleTypeSelected -> onScheduleTypeSelected(action)
                EditAlarmUiAction.DatePickerClicked -> onDatePickerClicked()
                EditAlarmUiAction.DatePickerDismissed -> _datePickerModel.value = null
                is EditAlarmUiAction.DateSelected -> onDateSelected(action)
                EditAlarmUiAction.SaveClicked -> saveAlarm()
                EditAlarmUiAction.BackClicked -> onBackClicked()
                EditAlarmUiAction.UnsavedChangesDiscardClicked -> onUnsavedChangesDiscardClicked()
                EditAlarmUiAction.UnsavedChangesDismissed -> _showUnsavedChangesDialog.value = false
                EditAlarmUiAction.UnsavedChangesSaveClicked -> {
                    _showUnsavedChangesDialog.value = false
                    saveAlarm()
                }
            }
        }
    }

    private suspend fun onDayOfWeekClicked(action: EditAlarmUiAction.DayOfWeekClicked) {
        val content = _state.value as? EditAlarmScreenModel.Content ?: return
        val repeating = content.scheduleContent as? ScheduleContent.Repeating ?: return
        val updated = repeating.dayOfWeeks.map { item ->
            if (item.text == action.item.text) item.copy(selected = !item.selected) else item
        }.toImmutableList()
        val updatedWeekDays = updated.mapNotNull { it.data.takeIf { _ -> it.selected } }.toSet()
        updateParams = updateParams.copy(
            schedule = UpdateParam.of(initialAlarm.schedule, Schedule.Repeating(updatedWeekDays))
        )
        rebuildState()
    }

    private suspend fun onScheduleTypeSelected(action: EditAlarmUiAction.ScheduleTypeSelected) {
        val newSchedule = if (action.isRepeating) {
            Schedule.Repeating(WeekDay.entries.toSet())
        } else {
            Schedule.OneTime(LocalDate.now())
        }
        updateParams = updateParams.copy(
            schedule = UpdateParam.of(initialAlarm.schedule, newSchedule)
        )
        rebuildState()
    }

    private fun onDatePickerClicked() {
        val selectedDate =
            (initialAlarm.applyUpdates(updateParams).schedule as? Schedule.OneTime)?.date
        _datePickerModel.value = datePickerModelFactory.create(
            selectedDate = selectedDate,
        )
    }

    private suspend fun onDateSelected(action: EditAlarmUiAction.DateSelected) {
        _datePickerModel.value = null
        updateParams = updateParams.copy(
            schedule = UpdateParam.of(initialAlarm.schedule, Schedule.OneTime(action.date))
        )
        rebuildState()
    }

    private suspend fun onSolisTimeChanged(action: SolisTimeAction) {
        val content = _state.value as? EditAlarmScreenModel.Content ?: return
        val isDay = (action as? EditAlarmUiAction.DayNightToggled)?.isDay ?: content.isDay
        val updatedSolisTime = SolisTime(
            hour = (action as? EditAlarmUiAction.HourChanged)?.hour ?: content.hour,
            minute = (action as? EditAlarmUiAction.MinuteChanged)?.minute ?: content.minute,
            type = if (isDay) SolisTime.Type.Day else SolisTime.Type.Night,
        )
        updateParams = updateParams.copy(
            solisTime = UpdateParam.of(
                initialAlarm.solisTime, updatedSolisTime
            )
        )
        rebuildState()
    }

    private suspend fun rebuildState() {
        _state.value = screenModelFactory.create(initialAlarm, updateParams)
    }

    private suspend fun onBackClicked() {
        if (updateParams.hasChanged()) {
            _showUnsavedChangesDialog.value = true
        } else {
            _event.emit(EditAlarmViewModelEvent.NavigateBack)
        }
    }

    private suspend fun onUnsavedChangesDiscardClicked() {
        _showUnsavedChangesDialog.value = false
        _event.emit(EditAlarmViewModelEvent.NavigateBack)
    }

    private suspend fun saveAlarm() {
        val alarm = initialAlarm.applyUpdates(updateParams)
        when (upsertAlarm(alarm)) {
            is Response.Success -> _event.emit(EditAlarmViewModelEvent.SaveSuccess)
            is Response.Failure -> {
                // TODO - show error
            }
        }
    }
}
