package ca.arnaud.horasolis.domain.repository

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.usecase.solis.GetSolisDayParams

interface SolisRepository {
    suspend fun getSolisDay(params: GetSolisDayParams): Response<SolisDay, Throwable>
}
