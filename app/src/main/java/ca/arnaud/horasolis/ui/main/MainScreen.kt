package ca.arnaud.horasolis.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.main.City
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onCitySelected: (City) -> Unit,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
    onSaveClicked: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    model: MainScreenModel,
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
                    .padding(8.dp),
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
    model: MainScreenModel,
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
            if (model.loading == MainScreenModel.Loading.Saving) {
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
    model: MainScreenModel,
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

        val times = model.times
        val half = (times.size + 1) / 2
        val leftTimes = times.subList(0, half)
        val rightTimes = times.subList(half, times.size)
        val headerModifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
        val contentLoading = model.loading == MainScreenModel.Loading.Content
        Row(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                stickyHeader {
                    ListHeader(
                        modifier = headerModifier,
                        text = "\uD83C\uDF1E",
                    )
                }
                items(leftTimes) { timeItem ->
                    TimeItemRow(
                        modifier = Modifier.padding(bottom = 4.dp),
                        timeItem = timeItem,
                        loading = contentLoading,
                        onTimeChecked = onTimeChecked,
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                stickyHeader {
                    ListHeader(
                        modifier = headerModifier,
                        text = "\uD83C\uDF1A",
                    )
                }
                items(rightTimes) { timeItem ->
                    TimeItemRow(
                        modifier = Modifier.padding(bottom = 4.dp),
                        timeItem = timeItem,
                        loading = contentLoading,
                        onTimeChecked = onTimeChecked,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListHeader(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier.background(HoraSolisTheme.colors.surface),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = text,
            style = Typography.headlineMedium,
        )
    }
}

@Composable
private fun CityDropdown(
    modifier: Modifier = Modifier,
    selectedCity: City,
    onCitySelected: (City) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val cities = City.entries

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = modifier
                .menuAnchor()
                .clickable { expanded = true },
            value = stringResource(id = selectedCity.nameRes),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = stringResource(id = R.string.city_dropdown_label)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = city.nameRes)) },
                    onClick = {
                        expanded = false
                        onCitySelected(city)
                    }
                )
            }
        }
    }
}

@Composable
private fun TimeItemRow(
    modifier: Modifier = Modifier,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
    timeItem: TimeItem,
    loading: Boolean,
) {
    val color = if (timeItem.night) {
        HoraSolisTheme.colors.secondaryContainer
    } else {
        HoraSolisTheme.colors.surfaceContainer
    }
    val border = if (timeItem.highlight) BorderStroke(3.dp, HoraSolisTheme.colors.primary) else null

    Card(
        modifier = modifier,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = timeItem.checked,
                onCheckedChange = { isChecked ->
                    onTimeChecked(timeItem, isChecked)
                }
            )
            
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeItem.label,
                    style = Typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.placeholder(
                        visible = loading,
                        color = HoraSolisTheme.colors.onSurface.copy(alpha = 0.1f),
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = timeItem.hour,
                    style = Typography.bodyLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MainScreenPreview() {
    HoraSolisTheme {
        val previewTimes = List(24) { i ->
            TimeItem(
                number = i + 1,
                label = "Time ${i + 1}",
                hour = String.format("%02d:00", i),
                night = i > 11,
                checked = (i % 3 == 0),
                highlight = (i == 12),
            )
        }.toImmutableList()
        MainScreen(
            model = MainScreenModel(
                times = previewTimes
            ),
            onCitySelected = {},
            onTimeChecked = { _, _ -> },
            onSaveClicked = {},
            onSnackbarDismissed = {},
        )
    }
}