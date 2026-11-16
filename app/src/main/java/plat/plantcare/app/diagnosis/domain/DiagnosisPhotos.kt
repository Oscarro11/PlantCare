package plat.plantcare.app.diagnosis.domain

interface DiagnosisPhotos {
    suspend fun importPhoto(source: String): String
    suspend fun read(photo: String): ByteArray
    suspend fun delete(photo: String)
}
