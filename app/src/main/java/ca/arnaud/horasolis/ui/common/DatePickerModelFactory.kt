package ca.arnaud.horasolis.ui.common

import ca.arnaud.horasolis.domain.provider.TimeProvider
import java.time.LocalDate

class DatePickerModelFactory(
    private val timeProvider: TimeProvider,
) {

    fun create(
        selectedDate: LocalDate?,
    ): DatePickerModel = DatePickerModel(
        minDate = timeProvider.getNowDate(),
        initialSelectedDate = selectedDate,
    )
}
