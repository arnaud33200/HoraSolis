package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ca.arnaud.horasolis.extension.formatSunTime
import java.time.LocalTime

data class TimePickerDialogModel(
    val hour: Int = 12,
    val minute: Int = 0,
)

@Composable
fun TimePickerDialog(
    model: TimePickerDialogModel = TimePickerDialogModel(),
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = model.hour,
        initialMinute = model.minute
    )
    val sunTime = formatSunTime(timeState.hour, timeState.minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Column {
                TimePicker(state = timeState)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "$sunTime (${timeState.hour}:${timeState.minute})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(timeState.hour, timeState.minute))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    MaterialTheme {
        TimePickerDialog(
            model = TimePickerDialogModel(hour = 8, minute = 30),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
