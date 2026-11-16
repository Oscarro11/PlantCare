package plat.plantcare.app.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import plat.plantcare.app.core.data.plant.characteristics.local.PlantCharacteristicsDAO
import plat.plantcare.app.core.data.plant.characteristics.local.PlantCharacteristicsEntity
import plat.plantcare.app.core.data.plant.considerations.local.PlantConsiderationsDAO
import plat.plantcare.app.core.data.plant.considerations.local.PlantConsiderationsEntity
import plat.plantcare.app.core.data.plant.general.local.PlantDAO
import plat.plantcare.app.core.data.plant.general.local.PlantEntity
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PlantEntity::class,
        PlantCharacteristicsEntity::class,
        PlantConsiderationsEntity::class
    ],
    version = 10
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun plantDAO(): PlantDAO
    abstract fun plantCharacteristicsDAO(): PlantCharacteristicsDAO
    abstract fun plantConsiderationsDAO(): PlantConsiderationsDAO
}