package plat.plantcare.app.core.data.plant.identification

import kotlinx.serialization.Serializable

@Serializable
data class PlantNetResponseDTO(
    val scientificName: String,
    val commonNames: List<String>,
    val family: String?,
    val score: Float
) {
    val displayName: String get() = commonNames.firstOrNull() ?: scientificName
}

sealed class IdentificationError(cause: Throwable? = null) : Exception(cause) {
    class NoNetwork(cause: Throwable? = null) : IdentificationError(cause)
    class QuotaExceeded : IdentificationError()
    class InvalidApiKey : IdentificationError()
    class ServiceUnavailable : IdentificationError()
    class Unknown(cause: Throwable? = null) : IdentificationError(cause)
}