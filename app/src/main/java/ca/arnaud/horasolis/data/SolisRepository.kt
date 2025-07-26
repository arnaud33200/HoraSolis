package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.map
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.onSuccess
import ca.arnaud.horasolis.domain.usecase.GetSolisDayParams
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString

class SolisRepository(
    private val ktorClient: KtorClient
) {

    private val cacheSolisDayMap = mutableMapOf<String, SolisDay>()

    suspend fun getSolisDay(
        params: GetSolisDayParams,
    ): Response<SolisDay, Throwable> {
        val cacheKey = params.toCacheKey()
        cacheSolisDayMap[cacheKey]?.let { cachedSolisDay ->
            return Response.Success(cachedSolisDay)
        }

        val resource = GetSunTime(
            lat = params.location.lat,
            lng = params.location.lng,
            tzid = params.location.timZoneId,
            date = params.date.toIsoString(),
        )
        return ktorClient.getResponse<GetSunTime, RemoteSunTimeResponse>(resource).map(
            transform = { remoteSunTimeResponse ->
                SolisDay(
                    atDate = params.date,
                    civilSunriseTime = remoteSunTimeResponse.results.sunrise,
                    civilSunsetTime = remoteSunTimeResponse.results.sunset,
                )
            }
        ).onSuccess { solisDay ->
            cacheSolisDayMap[cacheKey] = solisDay
        }
    }

    private fun GetSolisDayParams.toCacheKey(): String {
        return "${location.lat},${location.lng},${location.timZoneId},${date.toIsoString()}"
    }
}