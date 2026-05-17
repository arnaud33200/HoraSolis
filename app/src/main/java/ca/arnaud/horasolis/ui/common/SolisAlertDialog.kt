package ca.arnaud.horasolis.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SolisAlertDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
    message: String,
    confirmLabel: String,
    dismissLabel: String? = null,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = title?.let { { Text(text = it) } },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel)
            }
        },
        dismissButton = dismissLabel?.let {
            {
                TextButton(onClick = onDismiss) {
                    Text(text = it)
                }
            }
        },
    )
}
