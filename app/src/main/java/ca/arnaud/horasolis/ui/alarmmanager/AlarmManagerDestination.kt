package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmManagerDestination(
    viewModel: AlarmManagerViewModel,
    onNavigateToEditAlarm: (alarmId: Int?) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val clockViewModel = koinViewModel<SolisClockViewModel>()
    val clockModel by clockViewModel.state.collectAsStateWithLifecycle()

    var showLocationDialog by remember { mutableStateOf(false) }

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
        onLocationClick = { showLocationDialog = true },
        onAlarmDeleteClick = viewModel::onAlarmDeleteClick,
        onAddClick = viewModel::onAddClick,
        onAlarmItemClick = viewModel::onAlarmItemClick,
        onAlarmToggleClick = viewModel::onAlarmToggleClick,
        clockModel = clockModel,
    )

    if (showLocationDialog) {
        EditLocationDialog(
            onDismissRequest = { showLocationDialog = false },
        )
    }
}
