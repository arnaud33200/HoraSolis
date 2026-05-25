package ca.arnaud.horasolis.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.editalarm.RingtonePickerResult
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

data class SettingsScreenModel(
    val soundName: String,
    val vibrate: Boolean,
)

sealed interface SettingsUiAction {
    data object SaveClicked : SettingsUiAction
    data object SoundPickerClicked : SettingsUiAction
    data class VibrationToggled(val enabled: Boolean) : SettingsUiAction
    data class SoundResult(val result: RingtonePickerResult) : SettingsUiAction
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAction: (SettingsUiAction) -> Unit,
    model: SettingsScreenModel,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.settings_screen_title),
                actions = {
                    IconButton(onClick = { onAction(SettingsUiAction.SaveClicked) }) {
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
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SoundSection(
                modifier = Modifier.fillMaxWidth(),
                soundName = model.soundName,
                onPickerClicked = { onAction(SettingsUiAction.SoundPickerClicked) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            VibrationSection(
                modifier = Modifier.fillMaxWidth(),
                vibrationEnabled = model.vibrate,
                onToggle = { onAction(SettingsUiAction.VibrationToggled(it)) },
            )

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun SoundSection(
    modifier: Modifier = Modifier,
    onPickerClicked: () -> Unit,
    soundName: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.alarm_sound_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = soundName,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        TextButton(onClick = onPickerClicked) {
            Text(stringResource(R.string.alarm_sound_change))
        }
    }
}

@Composable
private fun VibrationSection(
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
) {
    Row(
        modifier = modifier
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.alarm_vibration_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = vibrationEnabled,
            onCheckedChange = onToggle,
        )
    }
}

private class SettingsScreenPreviewProvider : PreviewParameterProvider<SettingsScreenModel> {

    override val values = sequenceOf(
        SettingsScreenModel(soundName = "Default", vibrate = true),
        SettingsScreenModel(soundName = "Cesium", vibrate = false),
    )
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsScreenPreviewProvider::class) model: SettingsScreenModel,
) {
    HoraSolisTheme {
        SettingsScreen(
            onBackClick = {},
            onAction = {},
            model = model,
        )
    }
}
