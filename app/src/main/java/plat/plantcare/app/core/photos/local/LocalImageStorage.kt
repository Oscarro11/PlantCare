package plat.plantcare.app.core.photos.local

interface LocalImageStorage {
    suspend fun importImage(source: String): String
    suspend fun readForUpload(image: String): ByteArray
    suspend fun delete(image: String)
    suspend fun clearStale()
}

fun String.isRemoteImage(): Boolean = startsWith("https://") || startsWith("http://")