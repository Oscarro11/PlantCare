package plat.plantcare.app.core.data.remote

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.User

data class UserDTO(
    @DocumentId val id: String = "",
    val name: String = "",
    val location: String = "",
    val image: String = "",
    val streakDays: Int = 0
)

fun UserDTO.toModel(): User =
    User(
        id = id,
        name = name,
        location = location,
        imageUrl = image,
        streakDays = streakDays
    )