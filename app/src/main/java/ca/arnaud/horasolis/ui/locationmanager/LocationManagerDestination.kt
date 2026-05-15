package ca.arnaud.horasolis.ui.locationmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.alarmmanager.EditLocationDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationManagerDestination(
    onBack: () -> Unit,
) {
    val viewModel: LocationManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val editLocationDialog by viewModel.editLocationDialog.collectAsStateWithLifecycle()

    LocationManagerScreen(
        model = state,
        onSelectLocation = viewModel::onSelectLocation,
        onEditLocation = viewModel::onEditLocation,
        onBack = onBack,
        onAddClick = viewModel::onAddClick,
    )

    editLocationDialog?.let { dialog ->
        EditLocationDialog(
            locationId = dialog.locationId,
            onDismissRequest = viewModel::onDialogDismiss,
        )
    }
}
