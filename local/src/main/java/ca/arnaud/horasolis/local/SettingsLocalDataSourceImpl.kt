package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.SettingsLocalDataSource
import ca.arnaud.horasolis.domain.model.alarm.Settings

class SettingsLocalDataSourceImpl(database: HoraSolisDatabase) : SettingsLocalDataSource {

    private val settingsDao = database.settingsDao()

    override suspend fun getSettings(): Settings? = settingsDao.get()?.toSettings()

    override suspend fun saveSettings(settings: Settings) = settingsDao.upsert(settings.toEntity())
}
