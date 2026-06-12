package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class ObserveSavedAlarmsUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<List<SavedAlarm>> = alarmRepository.getAlarmsFlow()
}
