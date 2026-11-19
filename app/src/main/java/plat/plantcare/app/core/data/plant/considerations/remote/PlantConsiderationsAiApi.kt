package plat.plantcare.app.core.data.plant.considerations.remote

interface PlantConsiderationsAiApi {
    suspend fun generateConsiderations(speciesName: String?, photo: ByteArray): PlantConsiderationsAiDTO
}