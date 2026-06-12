package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.repository.LogRepository

class ClearAlarmLogsUseCase(
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke() {
        logRepository.clearLogs()
    }
}
