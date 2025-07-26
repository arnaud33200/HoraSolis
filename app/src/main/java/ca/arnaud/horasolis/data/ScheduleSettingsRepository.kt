package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.usecase.SolisCivilTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.ScheduleSettingsEntity
import ca.arnaud.horasolis.local.SelectedTimeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalTime

class ScheduleSettingsRepository(
    database: HoraSolisDatabase,
) {

    private val settingsDao = database.settingsWithTimesDao()

    suspend fun saveScheduleSettings(scheduleSettings: ScheduleSettings) {
        val location = scheduleSettings.location
        val scheduleSettingsEntity = ScheduleSettingsEntity(
            lat = location.lat,
            lng = location.lng,
            timZoneId = location.timZoneId,
        )
        val timeEntities = scheduleSettings.selectedTime.map { time ->
            SelectedTimeEntity(
                number = time.number,
                scheduleSettingsId = scheduleSettingsEntity.id,
                startTime = time.startTime.toString(),
                duration = time.duration.toString(),
                type = time.type.name,
            )
        }
        settingsDao.saveSettingsAndTimes(
            settings = scheduleSettingsEntity,
            times = timeEntities,
        )
    }

    suspend fun getScheduleSettingsOrNull(): ScheduleSettings? {
        val settings = settingsDao.getSettings() ?: return null
        val selectedTimes = settingsDao.getAllTimes()

        return settings.toScheduleSettings(selectedTimes)
    }

    fun observeScheduleSettings(): Flow<ScheduleSettings?> {
        return settingsDao.observeSettingsAggregate().map { aggregate ->
            aggregate?.settings?.toScheduleSettings(aggregate.selectedTimes)
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
            selectedTime = selectedTimes.toSolisCivilTimes(),
        )
    }

    private fun List<SelectedTimeEntity>.toSolisCivilTimes(): List<SolisCivilTime> {
        return this.map { entity ->
            SolisCivilTime(
                number = entity.number,
                startTime = LocalTime.parse(entity.startTime),
                duration = Duration.parse(entity.duration),
                type = SolisCivilTime.Type.valueOf(entity.type)
            )
        }
    }
}