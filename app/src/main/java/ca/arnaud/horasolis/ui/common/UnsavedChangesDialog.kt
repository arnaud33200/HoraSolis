package ca.arnaud.horasolis.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.R

@Composable
fun UnsavedChangesDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    SolisAlertDialog(
        title = stringResource(R.string.unsaved_changes_dialog_title),
        message = stringResource(R.string.unsaved_changes_dialog_message),
        confirmLabel = stringResource(R.string.unsaved_changes_dialog_save_button),
        dismissLabel = stringResource(R.string.unsaved_changes_dialog_discard_button),
        onConfirm = onSave,
        onDismiss = onDiscard,
        onDismissRequest = onDismissRequest,
    )
}
