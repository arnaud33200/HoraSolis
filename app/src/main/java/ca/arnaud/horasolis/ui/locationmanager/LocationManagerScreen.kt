package ca.arnaud.horasolis.ui.locationmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface LocationManagerScreenModel {

    data object Empty : LocationManagerScreenModel

    data class Content(
        val currentLocationState: CurrentLocationState,
        val savedLocations: ImmutableList<LocationItemModel>,
    ) : LocationManagerScreenModel
}

data class LocationItemModel(
    val id: String,
    val name: String,
    val locationInfo: String,
)

sealed interface CurrentLocationState {

    data class Location(val item: LocationItemModel) : CurrentLocationState
    data object SelectLocation : CurrentLocationState
}

sealed interface LocationManagerUserAction {
    data class RowClick(val item: LocationItemModel) : LocationManagerUserAction
    data class EditClick(val item: LocationItemModel) : LocationManagerUserAction
    data class DeleteClick(val item: LocationItemModel) : LocationManagerUserAction
    data object AddClick : LocationManagerUserAction
}

@Composable
fun LocationManagerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAction: (LocationManagerUserAction) -> Unit,
    model: LocationManagerScreenModel,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.location_manager_screen_title),
                actions = {
                    IconButton(onClick = { onAction(LocationManagerUserAction.AddClick) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        when (model) {
            is LocationManagerScreenModel.Empty -> EmptyState(
                modifier = Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .fillMaxSize()
                    .padding(24.dp),
                onAction = onAction,
            )

            is LocationManagerScreenModel.Content -> LocationContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding(),
                onAction = onAction,
                model = model,
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onAction: (LocationManagerUserAction) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.location_manager_empty_message),
            textAlign = TextAlign.Center,
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { onAction(LocationManagerUserAction.AddClick) },
        ) {
            Text(text = stringResource(R.string.location_manager_empty_add_button))
        }
    }
}

@Composable
private fun LocationContent(
    modifier: Modifier = Modifier,
    onAction: (LocationManagerUserAction) -> Unit,
    model: LocationManagerScreenModel.Content,
) {
    LazyColumn(modifier = modifier) {
        item {
            SectionTitle(title = stringResource(R.string.location_manager_current_section_title))
        }
        item {
            when (val state = model.currentLocationState) {
                is CurrentLocationState.Location -> LocationItem(
                    onAction = onAction,
                    item = state.item,
                )

                CurrentLocationState.SelectLocation -> Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = stringResource(R.string.location_manager_select_location_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        item {
            SectionTitle(title = stringResource(R.string.location_manager_saved_section_title))
        }
        items(items = model.savedLocations) { item ->
            LocationItem(
                onAction = onAction,
                isSelectable = true,
                item = item,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun LocationItem(
    modifier: Modifier = Modifier,
    onAction: (LocationManagerUserAction) -> Unit,
    isSelectable: Boolean = false,
    item: LocationItemModel,
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)

    if (isSelectable) {
        Card(modifier = cardModifier, onClick = { onAction(LocationManagerUserAction.RowClick(item)) }) {
            LocationItemContent(onAction = onAction, item = item)
        }
    } else {
        Card(modifier = cardModifier) {
            LocationItemContent(onAction = onAction, item = item)
        }
    }
}

@Composable
private fun LocationItemContent(
    modifier: Modifier = Modifier,
    onAction: (LocationManagerUserAction) -> Unit,
    item: LocationItemModel,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 8.dp,
                top = 12.dp,
                bottom = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = item.name,
            )
            Text(
                text = item.locationInfo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onAction(LocationManagerUserAction.EditClick(item)) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
            )
        }
        IconButton(onClick = { onAction(LocationManagerUserAction.DeleteClick(item)) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
            )
        }
    }
}

private class LocationManagerScreenPreviewProvider :
    PreviewParameterProvider<LocationManagerScreenModel> {

    override val values = sequenceOf(
        LocationManagerScreenModel.Content(
            currentLocationState = CurrentLocationState.Location(
                item = LocationItemModel(
                    id = "1",
                    name = "Montreal",
                    locationInfo = "-73.6 / 45.5"
                ),
            ),
            savedLocations = persistentListOf(
                LocationItemModel(id = "1", name = "Montreal", locationInfo = "-73.6 / 45.5"),
                LocationItemModel(id = "2", name = "Paris", locationInfo = "2.3 / 48.8"),
            ),
        ),
        LocationManagerScreenModel.Empty,
        LocationManagerScreenModel.Content(
            currentLocationState = CurrentLocationState.SelectLocation,
            savedLocations = persistentListOf(
                LocationItemModel(id = "1", name = "Montreal", locationInfo = "-73.6 / 45.5"),
            ),
        ),
    )
}

@PreviewLightDark
@Composable
private fun LocationManagerScreenPreview(
    @PreviewParameter(LocationManagerScreenPreviewProvider::class) model: LocationManagerScreenModel,
) {
    HoraSolisTheme {
        LocationManagerScreen(
            model = model,
            onBackClick = {},
            onAction = {},
        )
    }
}
