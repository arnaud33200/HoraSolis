package ca.arnaud.horasolis.local

import ca.arnaud.horasolis.data.local.CachedSolisDay
import ca.arnaud.horasolis.data.local.SolisDayLocalDataSource

class SolisDayLocalDataSourceImpl(database: HoraSolisDatabase) : SolisDayLocalDataSource {

    private val solisDayDao = database.solisDayDao()

    override suspend fun getCached(cacheKey: String): CachedSolisDay? =
        solisDayDao.get(cacheKey)?.let {
            CachedSolisDay(
                civilSunriseTime = it.civilSunriseTime,
                civilSunsetTime = it.civilSunsetTime,
            )
        }

    override suspend fun upsert(cacheKey: String, solisDay: CachedSolisDay) =
        solisDayDao.upsert(
            SolisDayEntity(
                cacheKey = cacheKey,
                civilSunriseTime = solisDay.civilSunriseTime,
                civilSunsetTime = solisDay.civilSunsetTime,
            )
        )
}
