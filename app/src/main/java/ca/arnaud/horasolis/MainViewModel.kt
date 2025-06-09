package ca.arnaud.horasolis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.GetRomanTimesParams
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val getRomanTimes: GetRomanTimesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenModel>(MainScreenModel())
    val state: StateFlow<MainScreenModel> = _state

    init {
        viewModelScope.launch {
            val params = GetRomanTimesParams(
                lat = 48.8566,
                lng = 2.3522,
                date = LocalDate.now()
            )
            val result = getRomanTimes(params)
            _state.value = MainScreenModel(
                message = result.toString(),
            )
        }
    }
}