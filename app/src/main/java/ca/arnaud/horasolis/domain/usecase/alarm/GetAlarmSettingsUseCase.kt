package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.alarm.AlarmSettings

class GetAlarmSettingsUseCase {

    suspend operator fun invoke(): AlarmSettings {
        // TODO - read from a settings repository once global settings are implemented
        return AlarmSettings(
            ringtoneUrl = null,
            vibrate = true,
        )
    }
}
