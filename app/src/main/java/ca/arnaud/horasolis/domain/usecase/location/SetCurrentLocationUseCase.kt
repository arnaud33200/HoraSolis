package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.usecase.alarm.CancelAllAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.CheckAlarmScheduleUseCase

class SetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
    private val cancelAllAlarm: CancelAllAlarmUseCase,
    private val checkAlarmScheduleUseCase: CheckAlarmScheduleUseCase,
) {

    suspend operator fun invoke(id: String) {
        val location = locationRepository.getLocationOrNull(id) ?: return
        val currentLocation = locationRepository.getCurrentLocationOrNull()
        if (currentLocation?.id == location.id) return

        locationRepository.setCurrentLocation(id)

        /**
         * New location mean different local civil times.
         * the scheduled alarm date & time won't match anymore.
         * Cancel all alarm and reschedule them properly.
         */
        cancelAllAlarm()
        checkAlarmScheduleUseCase()
    }
}
