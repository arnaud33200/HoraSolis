package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.alarm.Settings
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.SettingsEntity
import ca.arnaud.horasolis.local.toSettings

class SettingsRepository(
    database: HoraSolisDatabase,
) {

    private val settingsDao = database.settingsDao()

    suspend fun getSettings(): Settings {
        return settingsDao.get()?.toSettings() ?: Settings(
            ringtoneUrl = null,
            vibrate = true,
        )
    }

    suspend fun saveSettings(settings: Settings) {
        settingsDao.upsert(
            SettingsEntity(
                ringtoneUrl = settings.ringtoneUrl,
                vibrate = settings.vibrate,
            )
        )
    }
}
