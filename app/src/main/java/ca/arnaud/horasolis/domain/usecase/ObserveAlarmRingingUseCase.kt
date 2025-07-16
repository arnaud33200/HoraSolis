package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.coroutines.flow.Flow

class ObserveAlarmRingingUseCase(
    private val alarmRepository: AlarmRepository,
) {

    operator fun invoke(): Flow<SavedAlarm?> = alarmRepository.getRingingFlow()
}