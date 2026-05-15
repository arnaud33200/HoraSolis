package ca.arnaud.horasolis.ui.common

import androidx.compose.runtime.Stable
import java.time.LocalDate

@Stable
data class DatePickerModel(
    val minDate: LocalDate,
    val initialSelectedDate: LocalDate? = null,
)
