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
            storage.getReferenceFromUrl(url).delete().await()
        } catch (e: StorageException) {
            if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) throw e
        }
    }
}