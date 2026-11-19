package plat.plantcare.app.core.data.user.remote

import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.user.domain.User
import plat.plantcare.app.core.data.user.local.UserEntity

@Serializable
data class UserDTO(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val image: String = "",
    val streakDays: Int = 0,
    val lastStreakDate: String = ""
)

fun UserDTO.toDomain(): User =
    User(
        id = id,
        name = name,
        location = location,
        imageUrl = image,
        streakDays = streakDays,
        lastStreakDate = lastStreakDate
    )

fun UserDTO.toEntity(): UserEntity =
    UserEntity(
        id = id,
        name = name,
        location = location,
        imageUrl = image,
        streakDays = streakDays,
        lastStreakDate = lastStreakDate
    )