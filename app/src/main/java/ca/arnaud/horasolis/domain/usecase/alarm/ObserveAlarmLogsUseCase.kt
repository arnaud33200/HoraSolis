package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.LogRepository
import ca.arnaud.horasolis.domain.model.AlarmLog
import kotlinx.coroutines.flow.Flow

class ObserveAlarmLogsUseCase(
    private val logRepository: LogRepository,
) {

    operator fun invoke(): Flow<List<AlarmLog>> {
        return logRepository.getLogsFlow()
    }
}
