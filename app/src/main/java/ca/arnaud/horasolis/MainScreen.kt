package ca.arnaud.horasolis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TimeItem(
    val label: String,
    val hour: String,
    val night: Boolean,
)

data class MainScreenModel(
    val message: String = "",
    val selectedCity: City = City.Thiviers,
    val times: ImmutableList<TimeItem> = persistentListOf(),
    val selectedTimes: ImmutableList<TimeItem> = persistentListOf(),
)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onCitySelected: (City) -> Unit,
    model: MainScreenModel,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
) {
    val selectedCity = model.selectedCity

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(40.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CityDropdown(
                    selectedCity = selectedCity,
                    onCitySelected = onCitySelected,
                )
            }

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(model.times) { timeItem ->
                    val color = if (timeItem.night) {
                        Color(0xff3774bd)
                    } else {
                        Color.Unspecified
                    }
                    val checked = model.selectedTimes.contains(timeItem)
                    Row(
                        modifier = Modifier.height(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                onTimeChecked(timeItem, isChecked)
                            }
                        )
                        Text(
                            text = timeItem.label,
                            color = color,
                        )
                        Spacer(modifier.weight(1f))
                        Text(
                            text = timeItem.hour,
                            color = color,
                        )
                    }

                }
            }

            Text(
                text = model.message
            )
        }

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

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    HoraSolisTheme {
        MainScreen(
            model = MainScreenModel(
                message = stringResource(id = R.string.app_name)
            ),
            onCitySelected = {},
            onTimeChecked = { _, _ -> },
        )
    }
}