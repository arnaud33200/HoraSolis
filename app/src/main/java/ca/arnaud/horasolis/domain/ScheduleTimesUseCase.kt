package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.RomanTimeAlarmService
import ca.arnaud.horasolis.data.HoraSolisDatabase
import ca.arnaud.horasolis.data.ScheduleSettingsEntity
import ca.arnaud.horasolis.data.SelectedTimeEntity

data class ScheduleTimesParams(
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
    val times: List<RomanTime>,
)

class ScheduleTimesUseCase(
    private val alarmService: RomanTimeAlarmService,
    private val database: HoraSolisDatabase,
    private val timeProvider: TimeProvider,
) {

    suspend operator fun invoke(params: ScheduleTimesParams) {
        saveParams(params)
        val nowDateTime = timeProvider.getNowDateTime()
        params.times.forEach { time ->
            val atDate = if (time.startTime.isBefore(nowDateTime.toLocalTime())) {
                nowDateTime.toLocalDate().plusDays(1)
            } else {
                nowDateTime.toLocalDate()
            }
            val alarmParams = RomanTimeAlarmScheduleParam(
                number = time.number,
                dateTime = atDate.atTime(time.startTime)
            )
            alarmService.scheduleAlarm(alarmParams)
        }
    }

    private suspend fun saveParams(params: ScheduleTimesParams) {
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