package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmManagerDestination(
    viewModel: AlarmManagerViewModel,
) {
    val state by viewModel.state.collectAsState()
    val timePickerDialogModel by viewModel.timePickerDialogModel.collectAsState()

    val clockViewModel = koinViewModel<SolisClockViewModel>()
    val clockModel by clockViewModel.state.collectAsState()

    // TODO - find a more elegant way to do that
    //  would need to figure out the next clock click with right frequency
    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            clockViewModel.refreshClock()
        }
    }

    var showLocationDialog by remember { mutableStateOf(false) }

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

    timePickerDialogModel?.let {
        EditSolisAlarmDialog(
            model = it,
            onConfirm = viewModel::onTimePicked,
            onDismiss = viewModel::onDialogDismiss
        )
    }

    if (showLocationDialog) {
        EditLocationDialog(
            onDismissRequest = { showLocationDialog = false },
        )
    }
}
