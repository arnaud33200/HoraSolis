package ca.arnaud.horasolis.ui.timelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.usecase.GetRomanTimesParams
import ca.arnaud.horasolis.domain.usecase.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.schedule.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.usecase.RomanTimes
import ca.arnaud.horasolis.domain.usecase.schedule.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.ui.common.HoraAlertDialogModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

class TimeListViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
    private val savedTimeSchedule: SavedTimeScheduleUseCase,
    private val observeSelectedTimes: ObserveSelectedTimesUseCase,
    private val screenModelFactory: TimeListScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow(screenModelFactory.createInitialLoading())
    val state: StateFlow<TimeListScreenModel> = _state

    private var currentRomanTimes: RomanTimes? = null
    private var savedSettings: ScheduleSettings? = null

    init {
        viewModelScope.launch {
            observeSelectedTimes().collectLatest { settings ->
                savedSettings = settings
                if (currentRomanTimes == null) {
                    val selectedCity = savedSettings?.let {
                        City.firstOrNull(it.location)
                    } ?: state.value.selectedCity
                    refreshTimes(selectedCity)
                } else {
                    updateScreenModel { model ->
                        screenModelFactory.updateWithSettings(model, settings)
                    }
                }
            }
        }

        viewModelScope.launch {
            while (true) {
                updateScreenModel { model ->
                    screenModelFactory.updateNowTime(model, currentRomanTimes)
                }
                delay(1.minutes)
            }
        }
    }

    private suspend fun refreshTimes(selectedCity: City) {
        val location = selectedCity.toUserLocation()
        val params = GetRomanTimesParams(
            location = location,
            date = LocalDate.now(),
        )

        _state.update { it.copy(loading = TimeListScreenModel.Loading.Content) }
        val romanTimes = getRomanTimes(params).getDataOrNull()
        _state.update { it.copy(loading = null) }
        if (romanTimes == null) {
            _state.update { it.copy(snackMessage = "Failed to load times") } // TODO hardcoded string
            return
        }

        currentRomanTimes = romanTimes
        updateScreenModel { model ->
            screenModelFactory.updateTimes(
                romanTimes,
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
            _state.update { it.copy(loading = TimeListScreenModel.Loading.Saving) }
            savedTimeSchedule(updatedSettings)
            updateScreenModel { model ->
                screenModelFactory.updateSavedSettings(model)
            }
        }
    }

    fun onSnackbarDismissed() {
        updateScreenModel { model ->
            model.copy(snackMessage = null)
        }
    }

    private fun updateScreenModel(
        update: (TimeListScreenModel) -> TimeListScreenModel
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