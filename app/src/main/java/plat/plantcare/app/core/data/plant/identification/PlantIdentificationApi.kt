package plat.plantcare.app.core.data.plant.identification

interface PlantIdentificationApi {
    suspend fun identify(photo: ByteArray, language: String = "es"): List<PlantNetResponseDTO>
}