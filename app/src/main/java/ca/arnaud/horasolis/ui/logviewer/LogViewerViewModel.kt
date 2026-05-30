package ca.arnaud.horasolis.ui.logviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.usecase.alarm.ClearAlarmLogsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveAlarmLogsUseCase
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogViewerViewModel(
    private val observeAlarmLogs: ObserveAlarmLogsUseCase,
    private val clearAlarmLogs: ClearAlarmLogsUseCase,
    private val dateFormatter: DateFormatter,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<LogViewerScreenModel>(LogViewerScreenModel.Empty)
    val state: StateFlow<LogViewerScreenModel> = _state

    fun onClearClick() {
        viewModelScope.launch {
            clearAlarmLogs()
        }
    }

    init {
        viewModelScope.launch {
            observeAlarmLogs().collectLatest { logs ->
                if (logs.isEmpty()) {
                    _state.value = LogViewerScreenModel.Empty
                } else {
                    val items = logs
                        .sortedByDescending { it.timestamp }
                        .map { log -> log.toItemModel() }
                        .toImmutableList()
                    _state.value = LogViewerScreenModel.Content(items = items)
                }
            }
        }
    }

    private fun AlarmLog.toItemModel(): LogItemModel {
        val timestampLabel = "${dateFormatter.formatDate(timestamp.toLocalDate())}, ${dateFormatter.formatCivilTime(timestamp.toLocalTime())}"
        return when (this) {
            is AlarmLog.Scheduled -> LogItemModel(
                id = id,
                alarmId = alarmId,
                typeLabel = stringProvider.getString(R.string.log_viewer_type_scheduled),
                timestampLabel = timestampLabel,
                detailLabel = "${dateFormatter.formatDate(scheduledDateTime.toLocalDate())} ${dateFormatter.formatCivilTime(scheduledDateTime.toLocalTime())}",
            )
            is AlarmLog.Cancelled -> LogItemModel(
                id = id,
                alarmId = alarmId,
                typeLabel = stringProvider.getString(R.string.log_viewer_type_cancelled),
                timestampLabel = timestampLabel,
                detailLabel = null,
            )
            is AlarmLog.Ringing -> LogItemModel(
                id = id,
                alarmId = alarmId,
                typeLabel = stringProvider.getString(R.string.log_viewer_type_ringing),
                timestampLabel = timestampLabel,
                detailLabel = null,
            )
        }
    }
}
