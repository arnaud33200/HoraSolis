package ca.arnaud.horasolis.ui.editalarm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditAlarmViewModel(
    val alarmId: Long?,
) : ViewModel() {

    private val _state = MutableStateFlow<EditAlarmScreenModel>(EditAlarmScreenModel.Loading)
    val state: StateFlow<EditAlarmScreenModel> = _state
}
