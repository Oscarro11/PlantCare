package plat.plantcare.app.core.data.plant.identification

import plat.plantcare.app.core.network.PlantNetApiKey

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantNetIdentificationApi @Inject constructor(
    private val client: HttpClient,
    @PlantNetApiKey private val apiKey: String
) : PlantIdentificationApi {

    override suspend fun identify(photo: ByteArray, language: String): List<PlantNetResponseDTO> =
        try {
            request(photo, language)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IdentificationError) {
            throw e
        } catch (e: IOException) {
            throw IdentificationError.NoNetwork(e)
        } catch (e: Exception) {
            throw IdentificationError.Unknown(e)
        }

    private suspend fun request(photo: ByteArray, language: String): List<PlantNetResponseDTO> {
        val response = client.submitFormWithBinaryData(
            url = ENDPOINT,
            formData = formData {
                append("organs", "auto")
                append("images", photo, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"photo.jpg\"")
                })
            }
        ) {
            parameter("api-key", apiKey)
            parameter("lang", language)
            parameter("nb-results", MAX_RESULTS)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body<PlantNetResponse>().toCandidates()
            HttpStatusCode.NotFound -> emptyList()
            HttpStatusCode.TooManyRequests -> throw IdentificationError.QuotaExceeded()
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden ->
                throw IdentificationError.InvalidApiKey()
            else -> throw if (response.status.value >= 500) {
                IdentificationError.ServiceUnavailable()
            } else {
                IdentificationError.Unknown(IllegalStateException("HTTP ${response.status}"))
            }
        }
    }

    private companion object {
        const val ENDPOINT = "v2/identify/all"
        const val MAX_RESULTS = 3
    }
}

@Serializable
internal data class PlantNetResponse(
    val results: List<PlantNetResult> = emptyList()
)

@Serializable
internal data class PlantNetResult(
    val score: Float,
    val species: PlantNetSpecies
)

@Serializable
internal data class PlantNetSpecies(
    val scientificNameWithoutAuthor: String,
    val commonNames: List<String> = emptyList(),
    val family: PlantNetTaxon? = null
)

@Serializable
internal data class PlantNetTaxon(
    val scientificNameWithoutAuthor: String
)

private fun PlantNetResponse.toCandidates(): List<PlantNetResponseDTO> =
    results.map { result ->
        PlantNetResponseDTO(
            scientificName = result.species.scientificNameWithoutAuthor,
            commonNames = result.species.commonNames,
            family = result.species.family?.scientificNameWithoutAuthor,
            score = result.score
        )
    }