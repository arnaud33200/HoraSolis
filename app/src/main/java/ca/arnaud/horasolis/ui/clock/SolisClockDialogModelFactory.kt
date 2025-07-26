package ca.arnaud.horasolis.ui.clock

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.extension.format

class SolisClockDialogModelFactory(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val clockModelFactory: SolisClockModelFactory,
) {

    suspend fun create(): SolisClockDialogModel {
        return when (val response = getSolisDay(timeProvider.getNowDate())) {
            is Response.Success -> {
                val solisDay = response.data
                val solisTime = timeProvider.getNowSolisTime(solisDay)
                val clockModel = clockModelFactory.create(solisDay, solisTime)
                SolisClockDialogModel.Content(
                    solisTime = solisTime.format(),
                    clock = clockModel,
                )
            }

            is Response.Failure -> SolisClockDialogModel.Error
        }
    }
}