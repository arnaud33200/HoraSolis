package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
import ca.arnaud.horasolis.ui.common.onQuickClicks
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography
import kotlinx.collections.immutable.persistentListOf

sealed interface AlarmManagerScreenModel {

    val snackMessage: String?

    data class Loading(
        override val snackMessage: String? = null,
    ) : AlarmManagerScreenModel

    data class MissingLocation(
        override val snackMessage: String? = null,
    ) : AlarmManagerScreenModel

    data class Content(
        override val snackMessage: String? = null,
        val list: AlarmListModel = AlarmListModel(),
    ) : AlarmManagerScreenModel
}

@Composable
fun AlarmManagerScreen(
    modifier: Modifier = Modifier,
    onAction: (AlarmManagerUiAction) -> Unit,
    clockModel: SolisClockWithTimeModel = SolisClockWithTimeModel.Loading,
    model: AlarmManagerScreenModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(model.snackMessage) {
        model.snackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onAction(AlarmManagerUiAction.SnackbarDismissed)
        }
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HoraTopBar(
                navigationIcon = {
                    IconButton(onClick = { onAction(AlarmManagerUiAction.SolisViewerClicked) }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(AlarmManagerUiAction.LocationClicked) }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.location_content_description),
                        )
                    }
                    IconButton(onClick = { onAction(AlarmManagerUiAction.SettingsClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_content_description),
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (model !is AlarmManagerScreenModel.MissingLocation) {
                BottomBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .navigationBarsPadding(),
                    buttonModifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(),
                    onAddClick = { onAction(AlarmManagerUiAction.AddClicked) },
                )
            }
        }
    ) { innerPadding ->
        when (model) {
            is AlarmManagerScreenModel.Content -> Content(
                modifier = Modifier
                    .padding(innerPadding),
                model = model,
                clockModel = clockModel,
                onAction = onAction,
            )

            is AlarmManagerScreenModel.MissingLocation -> MissingLocation(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                onLocationClick = { onAction(AlarmManagerUiAction.LocationClicked) },
            )

            is AlarmManagerScreenModel.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MissingLocation(
    modifier: Modifier = Modifier,
    onLocationClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.alarm_manager_screen_missing_location_message),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLocationClick,
        ) {
            Text(
                text = stringResource(R.string.alarm_manager_screen_missing_location_button),
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onAction: (AlarmManagerUiAction) -> Unit,
    model: AlarmManagerScreenModel.Content,
    clockModel: SolisClockWithTimeModel,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SolisClockWithTime(
            modifier = Modifier
                .width(200.dp)
                .onQuickClicks(count = 5, onQuickClicks = { onAction(AlarmManagerUiAction.ScheduleViewerClicked) }),
            model = clockModel,
            onLocationSelected = { onAction(AlarmManagerUiAction.LocationSelected(it)) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        AlarmList(
            modifier = Modifier
                .fillMaxWidth(),
            model = model.list,
            onDelete = { onAction(AlarmManagerUiAction.AlarmDeleteClicked(it)) },
            onEdit = { onAction(AlarmManagerUiAction.AlarmItemClicked(it)) },
            onToggle = { item, enabled -> onAction(AlarmManagerUiAction.AlarmToggleClicked(item, enabled)) },
        )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(
            modifier = buttonModifier,
            onClick = onAddClick,
        ) {
            Text(
                text = stringResource(R.string.alarm_manager_screen_add_button),
                style = Typography.bodyLarge
            )
        }
    }
}

private val sampleClockModel = SolisClockWithTimeModel.Content(
    time = SolisTimeModel(
        hours = "05 ☀️ 06",
        seconds = "35",
    ),
    location = "Toronto, Canada",
    locations = persistentListOf(
        LocationDropdownItem(id = "1", name = "Toronto, Canada"),
        LocationDropdownItem(id = "2", name = "Montreal"),
    ),
    clock = SolisClockModel(
        dayStartAngle = 170f,
        dayEndAngle = 200f,
        needleAngle = 30f,
    ),
)

private val sampleAlarmList = AlarmListModel(
    items = persistentListOf(
        AlarmItemModel(
            id = 1,
            title = "5 ☀️ 06",
            label = "Morning",
            civilTime = "6:30 AM",
            isEnabled = true,
            schedule = "Monday to Friday"
        ),
        AlarmItemModel(
            id = 2,
            title = "10 🌚 54",
            label = null,
            civilTime = "11:00 PM",
            isEnabled = false,
            schedule = "Week-end"
        ),
        AlarmItemModel(
            id = 3,
            title = "12 ☀️ 00",
            label = null,
            civilTime = "12:00 PM",
            isEnabled = true,
            schedule = "Every day"
        ),
    )
)

private class AlarmManagerScreenPreviewProvider : PreviewParameterProvider<AlarmManagerScreenModel> {
    override val values = sequenceOf(
        AlarmManagerScreenModel.Content(list = sampleAlarmList),
        AlarmManagerScreenModel.MissingLocation(),
        AlarmManagerScreenModel.Loading(),
    )
}

@PreviewLightDark
@Composable
private fun AlarmManagerScreenPreview(
    @PreviewParameter(AlarmManagerScreenPreviewProvider::class) model: AlarmManagerScreenModel,
) {
    HoraSolisTheme {
        AlarmManagerScreen(
            model = model,
            clockModel = sampleClockModel,
            onAction = {},
        )
    }
}

@Preview(device = Devices.TABLET)
@Preview(device = Devices.TABLET, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AlarmManagerScreenTabletPreview(
    @PreviewParameter(AlarmManagerScreenPreviewProvider::class) model: AlarmManagerScreenModel,
) {
    HoraSolisTheme {
        AlarmManagerScreen(
            model = model,
            clockModel = sampleClockModel,
            onAction = {},
        )
    }
}
