package ca.arnaud.horasolis.ui.solisviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.common.DatePickerModel
import ca.arnaud.horasolis.ui.common.DatePickerDialog
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun SolisViewerDestination(
    onBack: () -> Unit,
) {
    val viewModel: SolisViewerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showDatePicker by viewModel.showDatePicker.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = viewModel::onDateSelected,
            onDismiss = viewModel::onDismissDatePicker,
            model = DatePickerModel(
                minDate = LocalDate.of(2000, 1, 1),
                initialSelectedDate = selectedDate,
            ),
        )
    }

    SolisViewerScreen(
        model = state,
        onBackClick = onBack,
        onAction = viewModel::onAction,
    )
}
