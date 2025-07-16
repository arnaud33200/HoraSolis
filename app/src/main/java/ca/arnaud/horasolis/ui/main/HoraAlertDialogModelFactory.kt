package ca.arnaud.horasolis.ui.main

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.extension.format
import ca.arnaud.horasolis.ui.common.HoraAlertDialogModel
import ca.arnaud.horasolis.ui.common.StringProvider

class HoraAlertDialogModelFactory(
    private val stringProvider: StringProvider,
) {

    fun create(alarm: SavedAlarm): HoraAlertDialogModel {
        return HoraAlertDialogModel(
            title = stringProvider.getString(
                R.string.ringing_alarm_dialog_title,
                alarm.solisTime.format(),
            ),
            message = stringProvider.getString(R.string.ringing_alarm_dialog_message),
        )
    }
}