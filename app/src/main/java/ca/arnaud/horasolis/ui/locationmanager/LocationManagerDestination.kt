package ca.arnaud.horasolis.ui.locationmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.common.SolisAlertDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationManagerDestination(
    onBack: () -> Unit,
    onNavigateToEditLocation: (locationId: String?) -> Unit,
) {
    val viewModel: LocationManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is LocationManagerViewModelEvent.NavigateToEditLocation ->
                    onNavigateToEditLocation(event.locationId)
            }
        }
    }

    LocationManagerScreen(
        model = state,
        onBackClick = onBack,
        onAction = viewModel::onAction,
    )

    when (val dialog = dialogState) {
        is LocationManagerDialog.ConfirmDelete -> SolisAlertDialog(
            onConfirm = viewModel::onConfirmDelete,
            onDismiss = viewModel::onDismissDialog,
            title = stringResource(R.string.location_manager_delete_confirm_title),
            message = stringResource(
                R.string.location_manager_delete_confirm_message,
                dialog.item.name,
            ),
            confirmLabel = stringResource(R.string.ok_button),
            dismissLabel = stringResource(R.string.cancel_button),
        )
        LocationManagerDialog.LastLocationError -> SolisAlertDialog(
            onConfirm = viewModel::onDismissDialog,
            onDismiss = viewModel::onDismissDialog,
            message = stringResource(R.string.location_manager_delete_last_location_error),
            confirmLabel = stringResource(R.string.ok_button),
        )
        null -> Unit
    }
}
