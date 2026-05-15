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

data class EditLocationDialogModel(val locationId: String?)

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

@Composable
fun LocationManagerScreen(
    modifier: Modifier = Modifier,
    onSelectLocation: (LocationItemModel) -> Unit,
    onEditLocation: (LocationItemModel) -> Unit,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    model: LocationManagerScreenModel,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBack,
                title = stringResource(R.string.location_manager_screen_title),
                actions = {
                    IconButton(onClick = onAddClick) {
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
                onAddClick = onAddClick,
            )

            is LocationManagerScreenModel.Content -> LocationContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding(),
                onSelectLocation = onSelectLocation,
                onEditLocation = onEditLocation,
                model = model,
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
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
            onClick = onAddClick,
        ) {
            Text(text = stringResource(R.string.location_manager_empty_add_button))
        }
    }
}

@Composable
private fun LocationContent(
    modifier: Modifier = Modifier,
    onSelectLocation: (LocationItemModel) -> Unit,
    onEditLocation: (LocationItemModel) -> Unit,
    model: LocationManagerScreenModel.Content,
) {
    LazyColumn(modifier = modifier) {
        item {
            SectionTitle(title = stringResource(R.string.location_manager_current_section_title))
        }
        item {
            when (val state = model.currentLocationState) {
                is CurrentLocationState.Location -> LocationItem(
                    onEditClick = onEditLocation,
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
                onSelectLocation = onSelectLocation,
                onEditClick = onEditLocation,
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
    onSelectLocation: ((LocationItemModel) -> Unit)? = null,
    onEditClick: ((LocationItemModel) -> Unit)? = null,
    item: LocationItemModel,
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)

    if (onSelectLocation != null) {
        Card(modifier = cardModifier, onClick = { onSelectLocation(item) }) {
            LocationItemContent(onEditClick = onEditClick?.let { { it(item) } }, item = item)
        }
    } else {
        Card(modifier = cardModifier) {
            LocationItemContent(onEditClick = onEditClick?.let { { it(item) } }, item = item)
        }
    }
}

@Composable
private fun LocationItemContent(
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    item: LocationItemModel,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = item.name,
        )
        Text(
            text = item.locationInfo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (onEditClick != null) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                )
            }
        }
    }
}

private class LocationManagerScreenPreviewProvider :
    PreviewParameterProvider<LocationManagerScreenModel> {

    override val values = sequenceOf(
        LocationManagerScreenModel.Empty,
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
            onSelectLocation = {},
            onEditLocation = {},
            onBack = {},
            onAddClick = {},
        )
    }
}
