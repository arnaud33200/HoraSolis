package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmManagerDestination(
    onNavigateToEditAlarm: (alarmId: Int?) -> Unit,
    onNavigateToLocationManager: () -> Unit,
) {
    val viewModel: AlarmManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val clockViewModel = koinViewModel<SolisClockViewModel>()
    val clockModel by clockViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is AlarmManagerViewModelEvent.NavigateToEditAlarm -> onNavigateToEditAlarm(event.alarmId)
            }
        }
    }

    AlarmManagerScreen(
        model = state,
        onSnackbarDismissed = {},
        onLocationClick = onNavigateToLocationManager,
        onAlarmDeleteClick = viewModel::onAlarmDeleteClick,
        onAddClick = viewModel::onAddClick,
        onAlarmItemClick = viewModel::onAlarmItemClick,
        onAlarmToggleClick = viewModel::onAlarmToggleClick,
        clockModel = clockModel,
    )
}
