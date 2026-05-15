package ca.arnaud.horasolis.ui.locationmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.alarmmanager.EditLocationDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationManagerDestination(
    onBack: () -> Unit,
) {
    val viewModel: LocationManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    LocationManagerScreen(
        model = state,
        onSelectLocation = viewModel::onSelectLocation,
        onBack = onBack,
        onAddClick = { showAddDialog = true },
    )

    if (showAddDialog) {
        EditLocationDialog(
            onDismissRequest = { showAddDialog = false },
        )
    }
}
