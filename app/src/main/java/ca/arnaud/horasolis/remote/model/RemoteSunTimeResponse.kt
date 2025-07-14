package ca.arnaud.horasolis.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class RemoteSunTimeResponse(
    val results: Result,
) {

    @Serializable
    data class Result(
        @Contextual
        val sunrise: LocalTime,
        @Contextual
        val sunset: LocalTime,
    )
}