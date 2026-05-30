package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.LogRepository

class ClearAlarmLogsUseCase(
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke() {
        logRepository.clearLogs()
    }
}
