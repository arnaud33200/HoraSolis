package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ca.arnaud.horasolis.ui.clock.SolisClockDialog

@Composable
fun AlarmManagerDestination(
    viewModel: AlarmManagerViewModel,
) {
    val state by viewModel.state.collectAsState()
    val timePickerDialogModel by viewModel.timePickerDialogModel.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var showClockDialog by remember { mutableStateOf(false) }

    AlarmManagerScreen(
        model = state,
        onSnackbarDismissed = {},
        onLocationClick = { showLocationDialog = true },
        onClockClick = { showClockDialog = true },
        onAlarmDeleteClick = viewModel::onAlarmDeleteClick,
        onAddClick = viewModel::onAddClick,
        onAlarmItemClick = viewModel::onAlarmItemClick
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
    if (showClockDialog) {
        SolisClockDialog(
            onDismissRequest = { showClockDialog = false },
        )
    }
}
