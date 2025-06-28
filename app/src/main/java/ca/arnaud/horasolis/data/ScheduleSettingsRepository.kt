package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.ScheduleSettingsEntity
import ca.arnaud.horasolis.local.SelectedTimeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalTime

class ScheduleSettingsRepository(
    database: HoraSolisDatabase,
) {

    private val settingsDao = database.scheduleSettingsDao()
    private val selectedTimeDao = database.selectedTimeDao()

    suspend fun getScheduleSettingsOrNull(): ScheduleSettings? {
        val settings = settingsDao.getSettings() ?: return null
        val selectedTimes = selectedTimeDao.getAll()

        return settings.toScheduleSettings(selectedTimes)
    }

    fun observeScheduleSettings(): Flow<ScheduleSettings?> {
        val scheduleFlow = settingsDao.observeAll().map { it.firstOrNull() }
        val selectedTimesFlow = selectedTimeDao.observeAll()
        return scheduleFlow.combine(selectedTimesFlow) { settings, selectedTimes ->
            settings?.toScheduleSettings(selectedTimes) ?: null
        }
    }

    private fun ScheduleSettingsEntity.toScheduleSettings(
        selectedTimes: List<SelectedTimeEntity>,
    ): ScheduleSettings {
        return ScheduleSettings(
            location = UserLocation(
                lat = lat,
                lng = lng,
                timZoneId = timZoneId,
            ),
            selectedTime = selectedTimes.toRomanTimes(),
        )
    }

    private fun List<SelectedTimeEntity>.toRomanTimes(): List<RomanTime> {
        return this.map { entity ->
            RomanTime(
                number = entity.number,
                startTime = LocalTime.parse(entity.startTime),
                duration = Duration.ofSeconds(entity.duration),
                type = RomanTime.Type.valueOf(entity.type)
            )
        }
    }
}