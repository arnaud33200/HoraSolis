package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.alarm.Settings
import ca.arnaud.horasolis.domain.repository.SettingsRepository
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.SettingsEntity
import ca.arnaud.horasolis.local.toSettings

class SettingsRepositoryImpl(
    database: HoraSolisDatabase,
) : SettingsRepository {

    private val settingsDao = database.settingsDao()

    override suspend fun getSettings(): Settings {
        return settingsDao.get()?.toSettings() ?: Settings(
            ringtoneUrl = null,
            vibrate = true,
        )
    }

    override suspend fun saveSettings(settings: Settings) {
        settingsDao.upsert(
            SettingsEntity(
                ringtoneUrl = settings.ringtoneUrl,
                vibrate = settings.vibrate,
            )
        )
    }
}
