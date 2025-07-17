package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography

data class AlarmManagerScreenModel(
    val snackMessage: String? = null,
    val list: AlarmListModel = AlarmListModel(),
)

@Composable
fun AlarmManagerScreen(
    modifier: Modifier = Modifier,
    onSnackbarDismissed: () -> Unit,
    onLocationClick: () -> Unit,
    onAlarmDeleteClick: (AlarmItemModel) -> Unit,
    onAddClick: () -> Unit,
    onAlarmItemClick: (AlarmItemModel) -> Unit,
    model: AlarmManagerScreenModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(model.snackMessage) {
        model.snackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onSnackbarDismissed()
        }
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onLocationClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.location_content_description)
                    )
                }
            }
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                onAddClick = onAddClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            AlarmList(
                modifier = Modifier
                    .fillMaxWidth(),
                model = model.list,
                onDelete = onAlarmDeleteClick,
                onEdit = onAlarmItemClick,
            )
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onAddClick,

        ) {
        Text(
            text = stringResource(R.string.alarm_manager_screen_add_button),
            style = Typography.bodyLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun AlarmManagerScreenPreview() {
    HoraSolisTheme {
        val sampleList = AlarmListModel(
            items = kotlinx.collections.immutable.persistentListOf(
                AlarmItemModel(id = 1, title = "5 \u2600\uFE0F 06", "12:45"), // 5 ☀️ 06
                AlarmItemModel(id = 2, title = "10 \uD83C\uDF1A 54", "12:45"), // 10 🌚 54
                AlarmItemModel(id = 3, title = "12 \u2600\uFE0F 00", "12:45") // 12 ☀️ 00
            )
        )
        AlarmManagerScreen(
            model = AlarmManagerScreenModel(
                list = sampleList
            ),
            onSnackbarDismissed = {},
            onLocationClick = {},
            onAlarmDeleteClick = {},
            onAddClick = {},
            onAlarmItemClick = {},
        )
    }
}
