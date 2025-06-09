package ca.arnaud.horasolis.remote.model

import io.ktor.resources.Resource
import kotlinx.serialization.Contextual

@Resource("/json")
class GetSunTime(
    val lat: Double,
    val lng: Double,
    val tzid: String,
    @Contextual
    val date: String,
)