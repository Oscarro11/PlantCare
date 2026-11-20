package plat.plantcare.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantNetApiKey

enum class Service(
    val url: String,
    val apiKey: String
) {
    PLANT_NET(
        url = "https://my-api.plantnet.org/",
        apiKey = "2b10iU4NXzehNjy293fww1cqe"
    )
}

object HttpClientFactory{
    fun create(service: Service): HttpClient {
        return HttpClient(CIO.create()) {
            install(Logging) {
                level = LogLevel.NONE
                logger = Logger.ANDROID
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 45_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                url(service.url)
                accept(ContentType.Application.Json)
            }
        }
    }
}