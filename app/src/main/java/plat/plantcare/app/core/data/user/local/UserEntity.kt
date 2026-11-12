package plat.plantcare.app.core.data.user.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import plat.plantcare.app.core.data.user.domain.User
import plat.plantcare.app.core.data.user.remote.UserFirebaseDTO

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val streakDays: Int,
    val lastStreakDate: String
)

fun UserEntity.toDomain(): User =
    User(id, name, location, imageUrl, streakDays, lastStreakDate)

fun UserEntity.toFirebaseDTO(): UserFirebaseDTO =
    UserFirebaseDTO(
        id = id,
        name = name,
        location = location,
        image = imageUrl,
        streakDays = streakDays,
        lastStreakDate = lastStreakDate
    )

