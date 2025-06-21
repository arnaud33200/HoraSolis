package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.data.HoraSolisDatabase
import ca.arnaud.horasolis.data.ScheduleSettingsEntity
import ca.arnaud.horasolis.data.SelectedTimeEntity

data class SavedTimeScheduleParams(
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
    val times: List<RomanTime>,
)

class SavedTimeScheduleUseCase(
    private val database: HoraSolisDatabase,
    private val scheduleRomanTime: ScheduleRomanTimeUseCase,
) {

    suspend operator fun invoke(params: SavedTimeScheduleParams) {
        saveParams(params)
        params.times.forEach(scheduleRomanTime::invoke)
    }

    /**
     * Temporary solution, we should either call a repository or local data source.
     */
    private suspend fun saveParams(params: SavedTimeScheduleParams) {
        val selectedTimeDao = database.selectedTimeDao()
        selectedTimeDao.deleteAll()
        params.times.forEach { time ->
            val entity = SelectedTimeEntity(
                number = time.number,
                startTime = time.startTime.toString(),
                duration = time.duration.toHours(),
                type = time.type.name,
            )
            selectedTimeDao.insert(entity)
        }

        val settingsDao = database.scheduleSettingsDao()
        val scheduleSettingsEntity = ScheduleSettingsEntity(
            lat = params.lat,
            lng = params.lng,
            timZoneId = params.timZoneId,
        )
        settingsDao.insert(scheduleSettingsEntity)
    }
}