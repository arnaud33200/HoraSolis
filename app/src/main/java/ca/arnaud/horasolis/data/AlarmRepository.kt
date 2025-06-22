package ca.arnaud.horasolis.data

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

class AlarmRepository(
    private val dataStore: DataStore<Preferences>,
) {

    companion object {

        private val alarmRingingPreferenceKey = booleanPreferencesKey("alarm_ringing")
    }

    suspend fun setAlarmRinging(ringing: Boolean) {
        dataStore.edit { preferences ->
            preferences[alarmRingingPreferenceKey] = ringing
        }
    }

    fun getRingingFlow(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[alarmRingingPreferenceKey] ?: false
        }
}