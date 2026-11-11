package plat.plantcare.app.core.data.user.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.documentFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserApi @Inject constructor (
    private val firestore: FirebaseFirestore
) : UserApi {

    private val usersRef get() = firestore.collection("usuarios")

    override fun observeProfile(uid: String): Flow<UserDTO?> =
        usersRef.document(uid)
            .documentFlow<UserFirebaseDTO>()
            .map { it?.toCommonDTO() }

    override suspend fun getProfile(uid: String): UserDTO? =
        usersRef.document(uid)
            .get(Source.SERVER)
            .await()
            .toObject(UserFirebaseDTO::class.java)
            ?.toCommonDTO()

    override suspend fun createProfile(
        uid: String,
        name: String,
        imageUrl: String
    ): UserDTO {
        val ref = usersRef.document(uid)
        return firestore.runTransaction { tx ->
            val snapshot = tx.get(ref)
            val existing = snapshot.toObject(UserFirebaseDTO::class.java)
            if (snapshot.exists() && existing != null) {
                existing.toCommonDTO()
            } else {
                val dto = UserFirebaseDTO(id = uid, name = name, image = imageUrl)
                tx.set(ref, dto)
                dto.toCommonDTO()
            }
        }.await()
    }

    override suspend fun updateProfile(
        uid: String,
        name: String,
        location: String,
        imageUrl: String
    ) {
        usersRef.document(uid).update(
            mapOf(
                "name" to name,
                "location" to location,
                "image" to imageUrl,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    override suspend fun setStreakDays(uid: String, days: Int) {
        usersRef.document(uid).update(
            mapOf(
                "streakDays" to days,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    override suspend fun deleteProfile(uid: String) {
        usersRef.document(uid).delete().await()
    }
}