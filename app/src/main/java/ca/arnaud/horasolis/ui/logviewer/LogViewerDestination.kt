package ca.arnaud.horasolis.ui.logviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogViewerDestination(
    onBack: () -> Unit,
) {
    val viewModel: LogViewerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LogViewerScreen(
        model = state,
        onBackClick = onBack,
        onClearClick = viewModel::onClearClick,
    )
}
