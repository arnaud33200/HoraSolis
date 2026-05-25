package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.arnaud.horasolis.domain.model.alarm.Settings

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val ringtoneUrl: String? = null,
    val vibrate: Boolean = true,
)

fun SettingsEntity.toSettings() = Settings(
    ringtoneUrl = ringtoneUrl,
    vibrate = vibrate,
)

fun Settings.toEntity() = SettingsEntity(
    ringtoneUrl = ringtoneUrl,
    vibrate = vibrate,
)
