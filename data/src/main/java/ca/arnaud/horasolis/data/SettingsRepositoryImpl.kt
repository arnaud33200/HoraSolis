package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.data.local.SettingsLocalDataSource
import ca.arnaud.horasolis.domain.model.alarm.Settings
import ca.arnaud.horasolis.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource,
) : SettingsRepository {

    override suspend fun getSettings(): Settings =
        localDataSource.getSettings() ?: Settings(ringtoneUrl = null, vibrate = true)

    override suspend fun saveSettings(settings: Settings) = localDataSource.saveSettings(settings)
}
