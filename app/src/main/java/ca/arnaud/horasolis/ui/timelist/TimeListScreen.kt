package ca.arnaud.horasolis.ui.timelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import ca.arnaud.horasolis.ui.common.CityDropdown
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onCitySelected: (City) -> Unit,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
    onSaveClicked: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    model: TimeListScreenModel,
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
        bottomBar = {
            MainScreenBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                model = model,
                onSaveClicked = onSaveClicked,
            )
        }
    ) { innerPadding ->
        MainScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            model = model,
            onCitySelected = onCitySelected,
            onTimeChecked = onTimeChecked,
        )
    }
}

@Composable
private fun MainScreenBottomBar(
    modifier: Modifier = Modifier,
    model: TimeListScreenModel,
    onSaveClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = model.showSaveButton,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Button(
            modifier = modifier,
            onClick = onSaveClicked,
            enabled = model.loading == null,

            ) {
            if (model.loading == TimeListScreenModel.Loading.Saving) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(R.string.save_schedule_button),
                    style = Typography.bodyLarge
                )
            }
        }
    }
}


@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    model: TimeListScreenModel,
    onCitySelected: (City) -> Unit,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
) {
    val selectedCity = model.selectedCity
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        CityDropdown(
            selectedCity = selectedCity,
            onCitySelected = onCitySelected,
        )
        Spacer(modifier = Modifier.height(10.dp))
        val contentLoading = model.loading == TimeListScreenModel.Loading.Content
        TimeCheckList(
            dayTimes = model.dayTimes,
            nightTimes = model.nightTimes,
            loading = contentLoading,
            onTimeChecked = onTimeChecked
        )
    }
}


@PreviewLightDark
@Composable
private fun MainScreenPreview() {
    HoraSolisTheme {
        MainScreen(
            model = TimeListScreenModel(
                dayTimes = TimeListModel(
                    description = "15:34",
                    times = List(12) { i ->
                        TimeItem(
                            number = i + 1,
                            label = "Day ${i + 1}",
                            hour = String.format("%02d:00", i),
                            checked = (i % 3 == 0),
                            highlight = (i == 6),
                        )
                    }.toImmutableList()
                ),
                nightTimes = TimeListModel(
                    description = "08:45",
                    times = List(12) { i ->
                        TimeItem(
                            number = i + 1,
                            label = "Night ${i + 1}",
                            hour = String.format("%02d:00", i),
                            checked = (i % 3 == 0),
                            highlight = (i == 6),
                        )
                    }.toImmutableList()
                )
            ),
            onCitySelected = {},
            onTimeChecked = { _, _ -> },
            onSaveClicked = {},
            onSnackbarDismissed = {},
        )
    }
}