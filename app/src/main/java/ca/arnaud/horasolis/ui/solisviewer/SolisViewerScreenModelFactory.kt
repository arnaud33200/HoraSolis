package ca.arnaud.horasolis.ui.solisviewer

import ca.arnaud.horasolis.ui.common.DateFormatter
import java.time.LocalDate

class SolisViewerScreenModelFactory(
    private val dateFormatter: DateFormatter,
) {

    fun create(date: LocalDate): SolisViewerScreenModel =
        SolisViewerScreenModel(dateLabel = dateFormatter.formatDate(date))
}
