package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import kotlinx.coroutines.flow.Flow

class ObserveSelectedTimesUseCase(
    private val scheduleSettingsRepository: ScheduleSettingsRepository,
) {
    operator fun invoke(): Flow<ScheduleSettings?> {
        return scheduleSettingsRepository.observeScheduleSettings()
    }
}
