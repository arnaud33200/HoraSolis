package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SolisClockWithTimeModel {

    data object Loading : SolisClockWithTimeModel
    data object Error : SolisClockWithTimeModel

    data class Content(
        val time: SolisTimeModel,
        val location: String,
        val locations: ImmutableList<LocationDropdownItem>,
        val isLocationLoading: Boolean = false,
        val isDateLoading: Boolean = false,
        val clock: SolisClockModel,
    ) : SolisClockWithTimeModel
}

data class LocationDropdownItem(
    val id: String,
    val name: String,
)

@Composable
fun SolisClockWithTime(
    modifier: Modifier = Modifier,
    model: SolisClockWithTimeModel,
    onLocationSelected: (String) -> Unit,
    clockSize: Dp = 200.dp,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (model) {
            is SolisClockWithTimeModel.Loading -> {
                CircularProgressIndicator()
            }

            is SolisClockWithTimeModel.Content -> {
                SolisTime(model = model.time)

                Spacer(modifier = Modifier.height(8.dp))

                if (model.isLocationLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LocationDropdown(
                        location = model.location,
                        locations = model.locations,
                        onLocationSelected = onLocationSelected,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(contentAlignment = Alignment.Center) {
                    SolisClock(
                        model = model.clock,
                        modifier = Modifier.size(clockSize)
                    )
                    if (model.isDateLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                }
            }

            SolisClockWithTimeModel.Error -> {
                Text(text = stringResource(id = R.string.solis_clock_error_message))
            }
        }
    }
}

@Composable
private fun LocationDropdown(
    modifier: Modifier = Modifier,
    location: String,
    locations: ImmutableList<LocationDropdownItem>,
    onLocationSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.clickable(enabled = locations.isNotEmpty()) { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (locations.isNotEmpty()) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            locations.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onLocationSelected(item.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

class SolisClockDialogModelPreviewProvider : PreviewParameterProvider<SolisClockWithTimeModel> {
    override val values = sequenceOf(
        SolisClockWithTimeModel.Content(
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
            )
        ),
        SolisClockWithTimeModel.Loading,
        SolisClockWithTimeModel.Error
    )
}

@PreviewLightDark
@Composable
private fun SolisClockWithTimePreview(
    @PreviewParameter(SolisClockDialogModelPreviewProvider::class) model: SolisClockWithTimeModel,
) {
    HoraSolisTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SolisClockWithTime(
                model = model,
                onLocationSelected = {},
            )
        }
    }
}
