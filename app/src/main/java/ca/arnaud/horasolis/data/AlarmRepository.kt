package ca.arnaud.horasolis.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AlarmRepository {

    // TODO - replace with a dataflow
    private val ringingStateFlow = MutableStateFlow(false)

    fun setAlarmRinging(ringing: Boolean) {
        ringingStateFlow.value = ringing
    }

    fun getRingingFlow(): Flow<Boolean> = ringingStateFlow
}