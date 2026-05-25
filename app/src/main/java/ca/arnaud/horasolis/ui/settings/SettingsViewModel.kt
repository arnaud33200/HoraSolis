package ca.arnaud.horasolis.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.alarm.Settings
import ca.arnaud.horasolis.domain.usecase.alarm.GetSettingsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SaveSettingsUseCase
import ca.arnaud.horasolis.ui.common.RingtoneProvider
import ca.arnaud.horasolis.ui.common.StringProvider
import ca.arnaud.horasolis.ui.editalarm.RingtonePickerResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SettingsViewModelEvent {

    data object SaveSuccess : SettingsViewModelEvent

    data class LaunchRingtonePicker(val currentUri: String?) : SettingsViewModelEvent
}

class SettingsViewModel(
    private val getSettings: GetSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase,
    private val ringtoneProvider: RingtoneProvider,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsScreenModel(soundName = "", vibrate = false)
    )
    val state: StateFlow<SettingsScreenModel> = _state

    private val _event = MutableSharedFlow<SettingsViewModelEvent>()
    val event: SharedFlow<SettingsViewModelEvent> = _event

    private var currentSettings: Settings = Settings(ringtoneUrl = null, vibrate = false)

    init {
        viewModelScope.launch {
            currentSettings = getSettings()
            _state.value = buildScreenModel(currentSettings)
        }
    }

    fun onAction(action: SettingsUiAction) {
        viewModelScope.launch {
            when (action) {
                SettingsUiAction.SaveClicked -> save()
                SettingsUiAction.SoundPickerClicked -> {
                    _event.emit(SettingsViewModelEvent.LaunchRingtonePicker(currentSettings.ringtoneUrl))
                }
                is SettingsUiAction.VibrationToggled -> {
                    currentSettings = currentSettings.copy(vibrate = action.enabled)
                    _state.value = buildScreenModel(currentSettings)
                }
                is SettingsUiAction.SoundResult -> {
                    when (val result = action.result) {
                        is RingtonePickerResult.Data -> {
                            currentSettings = currentSettings.copy(ringtoneUrl = result.uri)
                            _state.value = buildScreenModel(currentSettings)
                        }
                        RingtonePickerResult.Cancelled -> Unit
                        RingtonePickerResult.Error -> Unit
                    }
                }
            }
        }
    }

    private suspend fun save() {
        saveSettings(currentSettings)
        _event.emit(SettingsViewModelEvent.SaveSuccess)
    }

    private fun buildScreenModel(settings: Settings): SettingsScreenModel {
        val soundName = ringtoneProvider.getNameOrNull(settings.ringtoneUrl)
            ?: stringProvider.getString(R.string.alarm_sound_default)
        return SettingsScreenModel(
            soundName = soundName,
            vibrate = settings.vibrate,
        )
    }
}
