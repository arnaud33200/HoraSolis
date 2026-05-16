package ca.arnaud.horasolis.ui.editlocation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.common.HoraTextField
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

data class EditLocationScreenModel(
    val fieldStates: EditLocationFieldStates = EditLocationFieldStates(),
    val saveEnabled: Boolean = false,
    val latTextFieldEnabled: Boolean = true,
    val longTextFieldEnabled: Boolean = true,
    val currentLocationLoading: Boolean = false,
)

@Composable
fun EditLocationScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    model: EditLocationScreenModel,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBack,
                title = stringResource(R.string.edit_location_screen_title),
                actions = {
                    IconButton(
                        enabled = model.saveEnabled,
                        onClick = onSaveClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            HoraTextField(
                modifier = Modifier.fillMaxWidth(),
                state = model.fieldStates.name,
                label = stringResource(R.string.location_name_label),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            HoraTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = model.latTextFieldEnabled,
                state = model.fieldStates.latitude,
                label = stringResource(R.string.latitude_label),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            HoraTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = model.longTextFieldEnabled,
                state = model.fieldStates.longitude,
                label = stringResource(R.string.longitude_label),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !model.currentLocationLoading,
                onClick = onCurrentLocationClick,
            ) {
                if (model.currentLocationLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.current_location_button))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditLocationScreenPreview() {
    HoraSolisTheme {
        EditLocationScreen(
            onBack = {},
            onSaveClick = {},
            onCurrentLocationClick = {},
            model = EditLocationScreenModel(),
        )
    }
}
