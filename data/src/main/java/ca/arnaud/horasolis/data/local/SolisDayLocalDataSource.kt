package ca.arnaud.horasolis.data.local

import java.time.LocalTime

data class CachedSolisDay(
    val civilSunriseTime: LocalTime,
    val civilSunsetTime: LocalTime,
)

interface SolisDayLocalDataSource {
    suspend fun getCached(cacheKey: String): CachedSolisDay?
    suspend fun upsert(cacheKey: String, solisDay: CachedSolisDay)
}
