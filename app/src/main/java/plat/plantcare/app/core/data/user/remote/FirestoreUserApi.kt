package plat.plantcare.app.core.data.user.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.documentFlow
import java.time.LocalDate
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

    override suspend fun recordStreakDays(uid: String, today: LocalDate) {

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(usersRef.document())
            check(snapshot.exists()) { "User profile does not exist: $uid" }

            val currentDays = snapshot.getLong("streakDays")?.toInt() ?: 0
            val lastActivityDate = snapshot.getString("lastStreakDate")
                ?.let { date -> runCatching { LocalDate.parse(date) }.getOrNull() }

            when (lastActivityDate) {
                today -> currentDays // Already counted today.
                today.minusDays(1) -> {
                    val nextDays = currentDays + 1
                    transaction.update(
                        usersRef.document(),
                        mapOf(
                            "streakDays" to nextDays,
                            "lastStreakDate" to today.toString()
                        )
                    )
                }
                else -> {
                    transaction.update(
                        usersRef.document(),
                        mapOf(
                            "streakDays" to 1,
                            "lastStreakDate" to today.toString()
                        )
                    )
                }
            }
        }.await()
    }

    override suspend fun deleteProfile(uid: String) {
        usersRef.document(uid).delete().await()
    }
}