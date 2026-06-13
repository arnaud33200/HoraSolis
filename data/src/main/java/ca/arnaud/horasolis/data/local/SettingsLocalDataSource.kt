package ca.arnaud.horasolis.data.local

import ca.arnaud.horasolis.domain.model.alarm.Settings

interface SettingsLocalDataSource {
    suspend fun getSettings(): Settings?
    suspend fun saveSettings(settings: Settings)
}
