package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.alarm.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
}
