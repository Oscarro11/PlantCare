package plat.plantcare.app.core.data.user.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import plat.plantcare.app.core.data.user.domain.User

data class UserFirebaseDTO(
    @DocumentId val id: String = "",
    val name: String = "",
    val location: String = "",
    val image: String = "",
    val streakDays: Int = 0,
    val lastStreakDate: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun UserFirebaseDTO.toModel(): User =
    User(
        id = id,
        name = name,
        location = location,
        imageUrl = image,
        streakDays = streakDays,
        lastStreakDate = lastStreakDate
    )

fun UserFirebaseDTO.toCommonDTO(): UserDTO =
    UserDTO( id, name, location, image, streakDays, lastStreakDate )