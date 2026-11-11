package plat.plantcare.app.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
}