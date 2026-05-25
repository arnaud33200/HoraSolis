package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.SettingsRepository
import ca.arnaud.horasolis.domain.model.alarm.Settings

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(): Settings {
        return settingsRepository.getSettings()
    }
}
