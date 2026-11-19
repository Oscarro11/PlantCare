package plat.plantcare.app.core.photos.remote

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePhotoStorage @Inject constructor(
    private val storage: FirebaseStorage
) : OnlineImageStorage {

    override suspend fun upload(
        path: String,
        bytes: ByteArray,
        onProgress: (Int) -> Unit
    ): String {
        val ref = storage.reference.child(path)

        val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()

        val task = ref.putBytes(bytes, metadata)
        task.addOnProgressListener { snapshot ->
            if (snapshot.totalByteCount > 0) {
                onProgress((100 * snapshot.bytesTransferred / snapshot.totalByteCount).toInt())
            }
        }

        try {
            task.await()
        } catch (e: CancellationException) {
            task.cancel()
            throw e
        }
        return ref.downloadUrl.await().toString()
    }

    override suspend fun delete(url: String) {
        try {
            storage.imageReference(url).delete().await()
        } catch (e: StorageException) {
            if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) throw e
        }
    }
}
fun FirebaseStorage.imageReference(url: String): com.google.firebase.storage.StorageReference {
    val uri = android.net.Uri.parse(url)
    if (plat.plantcare.app.BuildConfig.USE_EMULATOR && uri.scheme == "http" &&
        uri.host in setOf("127.0.0.1", "10.0.2.2", "localhost")) {
        val segments = uri.pathSegments
        require(segments.size == 5 && segments[0] == "v0" && segments[1] == "b" &&
            segments[2] == reference.bucket && segments[3] == "o")
        return reference.child(segments[4])
    }
    return getReferenceFromUrl(url)
}
