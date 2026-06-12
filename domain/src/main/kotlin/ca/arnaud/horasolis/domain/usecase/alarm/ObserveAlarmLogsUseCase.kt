package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.AlarmLog
import ca.arnaud.horasolis.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class ObserveAlarmLogsUseCase(
    private val logRepository: LogRepository,
) {

    operator fun invoke(): Flow<List<AlarmLog>> = logRepository.getLogsFlow()
}
