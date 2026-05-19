package ca.arnaud.horasolis.ui.solisviewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import ca.arnaud.horasolis.ui.clock.LocationDropdownItem
import ca.arnaud.horasolis.ui.clock.SolisClockWithTime
import ca.arnaud.horasolis.ui.clock.SolisClockWithTimeModel
import ca.arnaud.horasolis.ui.clock.SolisClockModel
import ca.arnaud.horasolis.ui.clock.SolisTimeModel
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.persistentListOf

data class SolisViewerScreenModel(
    val dateLabel: String,
)

sealed interface SolisViewerUserAction {
    data object PreviousMonth : SolisViewerUserAction
    data object PreviousDay : SolisViewerUserAction
    data object NextDay : SolisViewerUserAction
    data object NextMonth : SolisViewerUserAction
    data object SelectDate : SolisViewerUserAction
    data object NowClick : SolisViewerUserAction
}

@Composable
fun SolisViewerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAction: (SolisViewerUserAction) -> Unit,
    onLocationSelected: (String) -> Unit = {},
    model: SolisViewerScreenModel,
    clockModel: SolisClockWithTimeModel = SolisClockWithTimeModel.Loading,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.solis_viewer_screen_title),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            SolisClockWithTime(
                model = clockModel,
                onLocationSelected = onLocationSelected,
                clockSize = 250.dp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateNavigationRow(
                onAction = onAction,
                dateLabel = model.dateLabel,
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onAction(SolisViewerUserAction.NowClick) },
            ) {
                Text(text = stringResource(R.string.generic_now))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DateNavigationRow(
    modifier: Modifier = Modifier,
    onAction: (SolisViewerUserAction) -> Unit,
    dateLabel: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onAction(SolisViewerUserAction.PreviousMonth) }) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                contentDescription = null,
            )
        }
        IconButton(onClick = { onAction(SolisViewerUserAction.PreviousDay) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
            )
        }

        TextButton(onClick = { onAction(SolisViewerUserAction.SelectDate) }) {
            Text(text = dateLabel)
        }

        IconButton(onClick = { onAction(SolisViewerUserAction.NextDay) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
        IconButton(onClick = { onAction(SolisViewerUserAction.NextMonth) }) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
            )
        }
    }
}

private class SolisViewerScreenPreviewProvider :
    PreviewParameterProvider<SolisViewerScreenModel> {

    override val values = sequenceOf(
        SolisViewerScreenModel(dateLabel = "Thursday May 15"),
    )
}

@PreviewLightDark
@Composable
private fun SolisViewerScreenPreview(
    @PreviewParameter(SolisViewerScreenPreviewProvider::class) model: SolisViewerScreenModel,
) {
    HoraSolisTheme {
        SolisViewerScreen(
            model = model,
            clockModel = SolisClockWithTimeModel.Content(
                time = SolisTimeModel(hours = "12 🌞 00", seconds = "30"),
                location = "Toronto, Canada",
                locations = persistentListOf(
                    LocationDropdownItem(id = "1", name = "Toronto, Canada"),
                    LocationDropdownItem(id = "2", name = "Montreal"),
                ),
                clock = SolisClockModel(
                    dayStartAngle = -90f,
                    dayEndAngle = 200f,
                    needleAngle = 30f,
                ),
            ),
            onBackClick = {},
            onAction = {},
        )
    }
}
