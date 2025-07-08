package ca.arnaud.horasolis.ui.timelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import ca.arnaud.horasolis.service.AlarmRingingService
import ca.arnaud.horasolis.ui.common.HoraAlertDialog

@Composable
fun TimeListDestination(
    viewModel: TimeListViewModel,
) {
    val state by viewModel.state.collectAsState()

    MainScreen(
        model = state,
        onCitySelected = viewModel::onCitySelected,
        onTimeChecked = viewModel::onTimeChecked,
        onSaveClicked = viewModel::onSaveClicked,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
    )
}
