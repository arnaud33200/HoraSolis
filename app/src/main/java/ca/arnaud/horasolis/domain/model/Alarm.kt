package ca.arnaud.horasolis.domain.model

sealed interface Alarm {

    val label: String?
    val solisTime: SolisTime
    val enabled: Boolean
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