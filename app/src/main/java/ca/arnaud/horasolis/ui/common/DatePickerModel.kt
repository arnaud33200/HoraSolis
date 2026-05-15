package ca.arnaud.horasolis.ui.common

import java.time.LocalDate

data class DatePickerModel(
    val minDate: LocalDate,
    val initialSelectedDate: LocalDate? = null,
)
