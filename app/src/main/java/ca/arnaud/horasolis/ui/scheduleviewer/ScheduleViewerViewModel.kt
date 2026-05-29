package ca.arnaud.horasolis.ui.scheduleviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.data.ScheduleRepository
import ca.arnaud.horasolis.domain.usecase.alarm.RefreshAllAlarmScheduleUseCase
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewerViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val dateFormatter: DateFormatter,
    private val stringProvider: StringProvider,
    private val refreshAllAlarmSchedule: RefreshAllAlarmScheduleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScheduleViewerScreenModel>(ScheduleViewerScreenModel.Empty())
    val state: StateFlow<ScheduleViewerScreenModel> = _state

    fun onRefreshClick() {
        viewModelScope.launch {
            _state.value = _state.value.withRefreshing(true)
            refreshAllAlarmSchedule()
            _state.value = _state.value.withRefreshing(false)
        }
    }

    init {
        viewModelScope.launch {
            scheduleRepository.getScheduledAlarmsFlow().collectLatest { schedules ->
                val sorted = schedules.sortedBy { it.scheduledDateTime }
                val isRefreshing = _state.value.isRefreshing
                if (sorted.isEmpty()) {
                    _state.value = ScheduleViewerScreenModel.Empty(isRefreshing = isRefreshing)
                } else {
                    val today = LocalDate.now()
                    val items = sorted.map { alarm ->
                        val date = alarm.scheduledDateTime.toLocalDate()
                        val dateLabel = when (date) {
                            today -> stringProvider.getString(R.string.schedule_viewer_date_today)
                            today.plusDays(1) -> stringProvider.getString(R.string.schedule_viewer_date_tomorrow)
                            else -> dateFormatter.formatDate(date)
                        }
                        ScheduleItemModel(
                            alarmId = alarm.alarmId,
                            dateLabel = dateLabel,
                            timeLabel = dateFormatter.formatCivilTime(alarm.scheduledDateTime.toLocalTime()),
                        )
                    }.toImmutableList()
                    _state.value = ScheduleViewerScreenModel.Content(items = items, isRefreshing = isRefreshing)
                }
            }
        }
    }

    private fun ScheduleViewerScreenModel.withRefreshing(isRefreshing: Boolean) = when (this) {
        is ScheduleViewerScreenModel.Empty -> copy(isRefreshing = isRefreshing)
        is ScheduleViewerScreenModel.Content -> copy(isRefreshing = isRefreshing)
    }
}
