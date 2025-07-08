package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.model.SavedAlarm
import kotlinx.coroutines.flow.Flow

class ObserveSavedAlarmsUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<List<SavedAlarm>> {
        return alarmRepository.getAlarmsFlow()
    }
}