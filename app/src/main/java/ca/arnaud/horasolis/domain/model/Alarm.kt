package ca.arnaud.horasolis.domain.model

sealed interface Alarm {

    val label: String?
    val solisTime: SolisTime
    val enabled: Boolean

    fun toSavedAlarm(id: Int): SavedAlarm {
        return SavedAlarm(
            id = id,
            label = label,
            solisTime = solisTime,
            enabled = enabled,
        )
    }
}

data class NewAlarm(
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
) : Alarm

data class SavedAlarm(
    val id: Int,
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
) : Alarm