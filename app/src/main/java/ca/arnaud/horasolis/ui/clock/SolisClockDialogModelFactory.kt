package ca.arnaud.horasolis.ui.clock

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.extension.format

class SolisClockDialogModelFactory(
    private val timeProvider: TimeProvider,
    private val clockModelFactory: SolisClockModelFactory,
) {

    fun create(response: Response<SolisDay, GetSolisDayError>): SolisClockDialogModel {
        return when (response) {
            is Response.Success -> {
                val solisDay = response.data
                val solisTime = timeProvider.getNowSolisTime(solisDay)
                val clockModel = clockModelFactory.create(solisDay, solisTime)
                SolisClockDialogModel.Content(
                    solisHours = solisTime.format(),
                    solisSeconds = "%02d".format(solisTime.seconds),
                    clock = clockModel,
                )
            }

            is Response.Failure -> SolisClockDialogModel.Error
        }
    }
}