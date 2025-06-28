package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.local.HoraSolisDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalTime

class ObserveSelectedTimesUseCase(
    private val database: HoraSolisDatabase,
) {
    operator fun invoke(): Flow<List<RomanTime>> {
        val selectedTimeDao = database.selectedTimeDao()
        return selectedTimeDao.observeAll().map { entities ->
            entities.map { entity ->
                RomanTime(
                    number = entity.number,
                    startTime = LocalTime.parse(entity.startTime),
                    duration = Duration.ofSeconds(entity.duration),
                    type = RomanTime.Type.valueOf(entity.type)
                )
            }
        }
    }
}

