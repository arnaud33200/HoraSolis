package ca.arnaud.horasolis.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.timelist.City

@Composable
fun CityDropdown(
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

