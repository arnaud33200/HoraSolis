package ca.arnaud.horasolis.ui.locationmanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationManagerDestination(
    onBack: () -> Unit,
    onNavigateToEditLocation: (locationId: String?) -> Unit,
) {
    val viewModel: LocationManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LocationManagerScreen(
        model = state,
        onSelectLocation = viewModel::onSelectLocation,
        onEditLocation = { item -> onNavigateToEditLocation(item.id) },
        onBack = onBack,
        onAddClick = { onNavigateToEditLocation(null) },
    )
}
