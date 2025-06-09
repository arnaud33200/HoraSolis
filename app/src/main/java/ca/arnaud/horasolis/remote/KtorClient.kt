package ca.arnaud.horasolis.remote

import ca.arnaud.horasolis.domain.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDate
import java.time.LocalTime


class KtorClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        serializersModule = SerializersModule {
            contextual(LocalTime::class, LocalTimeSerializer)
            contextual(LocalDate::class, LocalDateSerializer)
        }
    }

    val client = HttpClient(CIO) {
        install(Resources)
        defaultRequest {
            url("https://api.sunrise-sunset.org")
        }
        install(ContentNegotiation) {
            json(json)
        }
    }


    suspend inline fun <reified Resource : Any, reified Data> getResponse(
        resource: Resource,
    ): Response<Data, Throwable> {
        return try {
            val response = client.get(resource).body() as Data
            Response.Success(response)
        } catch (exception: Throwable) {
            if (exception is CancellationException) throw exception
            Response.Failure(exception)
        }
    }
}

