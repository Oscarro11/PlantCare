package plat.plantcare.app.core.photos.remote

interface OnlineImageStorage {
    /** Uploads already-compressed JPEG bytes and returns the download URL. */
    suspend fun upload(path: String, bytes: ByteArray, onProgress: (Int) -> Unit = {}): String

    /** Idempotent: deleting a photo that no longer exists is not an error. */
    suspend fun delete(url: String)
}