package plat.plantcare.app.core.data

import plat.plantcare.app.core.data.tasks.TaskEntity
import plat.plantcare.app.core.data.tasks.TaskWrite
import plat.plantcare.app.core.data.tasks.TaskDao
import androidx.room.Database
import androidx.room.RoomDatabase
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.UserEntity

@Database(
    entities = [
        UserEntity::class, TaskEntity::class, TaskWrite::class
    ],
    version = 3
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDAO(): UserDAO
}