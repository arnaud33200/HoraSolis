package ca.arnaud.horasolis.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.R

data class HoraAlertDialogModel(
    val title: String,
    val message: String,
)

@Composable
fun HoraAlertDialog(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    model: HoraAlertDialogModel,
) {
    AlertDialog(
        onDismissRequest = {}, // Not dismissable by outside touch or back press
        confirmButton = {
            Button(onClick = onButtonClick) {
                Text(stringResource(id = R.string.ringing_alarm_dialog_button))
            }
        },
        title = {
            Text(model.title)
        },
        text = {
            Text(model.message)
        },
        modifier = modifier
    )
}