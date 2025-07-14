package ca.arnaud.horasolis.data

import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.map
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.usecase.GetSolisDayParams
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.remote.model.GetSunTime
import ca.arnaud.horasolis.remote.model.RemoteSunTimeResponse
import ca.arnaud.horasolis.remote.toIsoString

class SolisRepository(
    private val ktorClient: KtorClient
) {

    suspend fun getSolisDay(
        params: GetSolisDayParams,
    ): Response<SolisDay, Throwable> {
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
        )
    }
}