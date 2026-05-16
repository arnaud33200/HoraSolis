package ca.arnaud.horasolis.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalTime


/**
 * Example when it's night all the time, the astronomical_twilight begin and end are not the ma,e
 * ```json
 * {
 *     "results": {
 *         "sunrise": "7:00:00 PM",
 *         "sunset": "7:00:00 PM",
 *         "solar_noon": "5:31:40 AM",
 *         "day_length": "00:00:00",
 *         "civil_twilight_begin": "7:00:00 PM",
 *         "civil_twilight_end": "7:00:00 PM",
 *         "nautical_twilight_begin": "4:04:36 AM",
 *         "nautical_twilight_end": "6:58:44 AM",
 *         "astronomical_twilight_begin": "1:07:07 AM",
 *         "astronomical_twilight_end": "9:56:13 AM"
 *     },
 *     "status": "OK",
 *     "tzid": "America/Toronto"
 * }
 * ```
 *
 * but when it's day all the time, the twilight times are the same as well
 *```json
 * {
 *     "results": {
 *         "sunrise": "7:00:01 PM",
 *         "sunset": "7:00:01 PM",
 *         "solar_noon": "6:32:29 AM",
 *         "day_length": "00:00:00",
 *         "civil_twilight_begin": "7:00:01 PM",
 *         "civil_twilight_end": "7:00:01 PM",
 *         "nautical_twilight_begin": "7:00:01 PM",
 *         "nautical_twilight_end": "7:00:01 PM",
 *         "astronomical_twilight_begin": "7:00:01 PM",
 *         "astronomical_twilight_end": "7:00:01 PM"
 *     },
 *     "status": "OK",
 *     "tzid": "America/Toronto"
 * }
 * ```
 */
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