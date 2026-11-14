package plat.plantcare.app.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import plat.plantcare.app.core.data.plant.general.local.PlantDAO
import plat.plantcare.app.core.data.plant.general.local.PlantEntity
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PlantEntity::class
    ],
    version = 5
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun plantDAO(): PlantDAO
}