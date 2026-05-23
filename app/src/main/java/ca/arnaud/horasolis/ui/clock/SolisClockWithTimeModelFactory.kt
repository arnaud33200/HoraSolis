package ca.arnaud.horasolis.ui.clock

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.extension.format
import kotlinx.collections.immutable.toImmutableList

class SolisClockWithTimeModelFactory(
    private val timeProvider: TimeProvider,
    private val clockModelFactory: SolisClockModelFactory,
) {

    fun create(
        response: Response<SolisDay, GetSolisDayError>,
        solisClockData: SolisClockData,
        params: SolisClockViewModelParams,
    ): SolisClockWithTimeModel {
        return when (response) {
            is Response.Success -> {
                val solisDay = response.data
                val solisTime = timeProvider.getNowSolisTime(solisDay)
                val alarms = when (params) {
                    SolisClockViewModelParams.Default -> solisClockData.alarms
                    SolisClockViewModelParams.ViewOnly -> emptyList()
                }.filter { savedAlarm ->
                    savedAlarm.isActive(solisDay, timeProvider.getNowDateTime())
                }
                val clockModel = clockModelFactory.create(
                    solisDay, solisTime, alarms,
                )
                val location = solisDay.location.name.ifBlank { solisDay.location.timZoneId }
                SolisClockWithTimeModel.Content(
                    time = SolisTimeModel(
                        hours = solisTime.format(),
                        seconds = "%02d".format(solisTime.seconds),
                    ),
                    clock = clockModel,
                    location = location,
                    locations = solisClockData.locations.map {
                        LocationDropdownItem(id = it.id, name = it.name.ifBlank { it.id })
                    }.toImmutableList(),
                )
            }

            is Response.Failure -> SolisClockWithTimeModel.Error
        }
    }
}