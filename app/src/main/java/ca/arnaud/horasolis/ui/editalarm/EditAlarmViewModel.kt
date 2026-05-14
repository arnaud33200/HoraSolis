package ca.arnaud.horasolis.ui.editalarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.AlarmUpdateParams
import ca.arnaud.horasolis.domain.model.alarm.applyUpdates
import ca.arnaud.horasolis.domain.usecase.alarm.GetAlarmParams
import ca.arnaud.horasolis.domain.usecase.alarm.GetAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal sealed interface EditAlarmViewModelParams {

    data object New : EditAlarmViewModelParams

    data class Edit(
        val alarmId: Int,
    ) : EditAlarmViewModelParams
}

/**
 * ViewModel for [EditAlarmScreen].
 *
 * @property params Params to determine if edit or new alarm.
 *  Used to get new default alarm or existing alarm to edit.
 */
internal class EditAlarmViewModel(
    private val params: EditAlarmViewModelParams,
    private val getAlarm: GetAlarmUseCase,
    private val upsertAlarm: UpsertAlarmUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<EditAlarmScreenModel>(EditAlarmScreenModel.Loading)
    val state: StateFlow<EditAlarmScreenModel> = _state

    /**
     * Either the new alarm or the existing alarm without changes.
     * used to combine with [updateParams] when saving alarm.
     * Default to empty to avoid having nullable and check null everywhere.
     */
    private var initialAlarm: Alarm = Alarm.empty

    /**
     * All the changes made by the user.
     * Used to combine with [initialAlarm] when saving alarm.
     * Can be used to either reset initial values or check if any changes were made.
     */
    private var updateParams: AlarmUpdateParams = AlarmUpdateParams()

    init {
        viewModelScope.launch {
            val getParams = when (params) {
                EditAlarmViewModelParams.New -> GetAlarmParams.New
                is EditAlarmViewModelParams.Edit -> GetAlarmParams.Existing(
                    alarmId = params.alarmId,
                )
            }
            when (val response = getAlarm(getParams)) {
                is Response.Success -> {
                    initialAlarm = response.data
                    // TODO - update state
                }
                is Response.Failure -> {
                    // TODO - handle error state
                }
            }
        }
    }

    private suspend fun saveAlarm() {
        val alarm = initialAlarm.applyUpdates(updateParams)
        upsertAlarm(alarm)
    }

}
