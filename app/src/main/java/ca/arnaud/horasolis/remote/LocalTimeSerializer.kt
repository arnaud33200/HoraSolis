package ca.arnaud.horasolis.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer : KSerializer<LocalTime> {

    private val formatter = DateTimeFormatter.ofPattern("h:mm:ss a")

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        val dateTimeString = decoder.decodeString()
        return LocalTime.parse(dateTimeString, formatter)
    }
}
