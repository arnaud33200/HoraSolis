package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.usecase.GetRomanTimesParams
import ca.arnaud.horasolis.domain.usecase.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.usecase.RomanTimes
import ca.arnaud.horasolis.domain.usecase.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.domain.usecase.SetAlarmRingingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
    private val savedTimeSchedule: SavedTimeScheduleUseCase,
    private val observeSelectedTimes: ObserveSelectedTimesUseCase,
    private val observeAlarmRinging: ObserveAlarmRingingUseCase,
    private val setAlarmRinging: SetAlarmRingingUseCase,
    private val screenModelFactory: MainScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    // TODO - setup a model so we can tell which alarm is ringing
    private val _ringingDialog = MutableStateFlow(false)
    val ringingDialog: StateFlow<Boolean> = _ringingDialog

    private var currentRomanTimes: RomanTimes? = null
    private var savedSettings: ScheduleSettings? = null

    init {
        viewModelScope.launch {
            val selectedCity = state.value.selectedCity
            refreshTimes(selectedCity)

            observeSelectedTimes().collectLatest { settings ->
                savedSettings = settings
                updateScreenModel { model ->
                    screenModelFactory.updateWithSettings(model, settings)
                }
            }
        }

        viewModelScope.launch {
            observeAlarmRinging().collectLatest { ringing ->
                _ringingDialog.value = ringing
            }
        }
    }

    private suspend fun refreshTimes(selectedCity: City) {
        val location = selectedCity.toUserLocation()
        val params = GetRomanTimesParams(
            location = location,
            date = LocalDate.now(),
        )

        _state.update { it.copy(loading = MainScreenModel.Loading.Content) }
        val romanTimes = getRomanTimes(params).getDataOrNull()
        _state.update { it.copy(loading = null) }

        val times = romanTimes?.times ?: emptyList()
        currentRomanTimes = romanTimes
        updateScreenModel { model ->
            screenModelFactory.updateTimes(
                times,
                savedSettings,
                model.copy(selectedCity = selectedCity)
            )
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            refreshTimes(city)
        }
    }

    fun onTimeChecked(timeItem: TimeItem, checked: Boolean) {
        updateScreenModel { model ->
            screenModelFactory.updateSelectedTimes(model, timeItem, checked)
        }
    }

    fun onSaveClicked() {
        val updatedSettings = state.value.getUpdatedScheduleSettings(currentRomanTimes)
            ?: return
        viewModelScope.launch {
            _state.update { it.copy(loading = MainScreenModel.Loading.Saving) }
            savedTimeSchedule(updatedSettings)
            _state.update { it.copy(loading = null) }
        }
    }

    /**
     * Fallback when the service is already stopped but ringing state is still true.
     * This can happen if the service was stopped manually or due to an error.
     */
    fun onStopRingingServiceFailed() {
        viewModelScope.launch {
            setAlarmRinging(false)
        }
    }

    private fun updateScreenModel(
        update: (MainScreenModel) -> MainScreenModel
    ) {
        _state.update { currentModel ->
            val newModel = update(currentModel)
            val updatedSettings = newModel.getUpdatedScheduleSettings(currentRomanTimes)
            newModel.copy(
                showSaveButton = savedSettings != updatedSettings,
            )
        }
    }
}