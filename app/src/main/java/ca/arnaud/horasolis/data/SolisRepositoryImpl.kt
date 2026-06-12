package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.common.map
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.common.onSuccess
import ca.arnaud.horasolis.domain.repository.SolisRepository
import ca.arnaud.horasolis.domain.usecase.solis.GetSolisDayParams
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.local.SolisDayEntity
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString

class SolisRepositoryImpl(
    private val ktorClient: KtorClient,
    database: HoraSolisDatabase,
) : SolisRepository {

    private val solisDayDao = database.solisDayDao()

    override suspend fun getSolisDay(
        params: GetSolisDayParams,
    ): Response<SolisDay, Throwable> {
        val cacheKey = params.toCacheKey()

        solisDayDao.get(cacheKey)?.let { entity ->
            return Response.Success(entity.toSolisDay(params))
        }

        val location = params.location
        val resource = GetSunTime(
            lat = location.lat,
            lng = location.lng,
            tzid = location.timZoneId,
            date = params.date.toIsoString(),
        )
        return ktorClient.getResponse<GetSunTime, RemoteSunTimeResponse>(resource).map(
            transform = { remoteSunTimeResponse ->
                SolisDay(
                    atDate = params.date,
                    civilSunriseTime = remoteSunTimeResponse.results.sunrise,
                    civilSunsetTime = remoteSunTimeResponse.results.sunset,
                    location = location,
                )
            }
        ).onSuccess { solisDay ->
            solisDayDao.upsert(SolisDayEntity(
                cacheKey = cacheKey,
                civilSunriseTime = solisDay.civilSunriseTime,
                civilSunsetTime = solisDay.civilSunsetTime,
            ))
        }
    }

    private fun SolisDayEntity.toSolisDay(params: GetSolisDayParams) = SolisDay(
        atDate = params.date,
        civilSunriseTime = civilSunriseTime,
        civilSunsetTime = civilSunsetTime,
        location = params.location,
    )

    private fun GetSolisDayParams.toCacheKey(): String {
        return "${location.lat},${location.lng},${location.timZoneId},${date.toIsoString()}"
    }
}
