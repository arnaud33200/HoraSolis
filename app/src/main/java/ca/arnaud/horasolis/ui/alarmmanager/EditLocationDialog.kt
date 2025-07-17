package ca.arnaud.horasolis.ui.alarmmanager

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.ui.common.HoraTextField
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

data class EditLocationDialogModel(
    val latitude: TextFieldState = TextFieldState(),
    val longitude: TextFieldState = TextFieldState(),
    val saveEnabled: Boolean = false,
    val requestDismiss: Boolean = false,
)

@Composable
fun EditLocationDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    val viewModel: EditLocationViewModel = koinViewModel()
    val model by viewModel.state.collectAsState()

    val currentLocationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { granted ->
            val permissionResult = when (granted) {
                true -> PermissionResult.Granted
                false -> PermissionResult.Denied
            }
            viewModel.onCurrentLocationClick(permissionResult)
        }
    )

    LaunchedEffect(model.requestDismiss) {
        if (model.requestDismiss) {
            onDismissRequest()
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                enabled = model.saveEnabled,
                onClick = viewModel::onSaveLocationClick,
            ) {
                Text(stringResource(id = R.string.edit_alarm_dialog_save_button))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(id = R.string.cancel_button))
            }
        },
        title = {
            Text(stringResource(id = R.string.current_location_button))
        },
        text = {
            Column {
                HoraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = model.latitude,
                    label = stringResource(id = R.string.latitude_label),
                )

                Spacer(modifier = Modifier.padding(8.dp))

                HoraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = model.longitude,
                    label = stringResource(id = R.string.longitude_label),
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = currentLocationPermissionState::launchPermissionRequest,
                ) {
                    Text(stringResource(id = R.string.current_location_button))
                }
            }
        },
    )
}

@PreviewLightDark
@Composable
fun EditLocationDialogPreview() {
    HoraSolisTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EditLocationDialog(
                onDismissRequest = {},
            )
        }
    }
}
