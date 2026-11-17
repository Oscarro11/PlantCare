package plat.plantcare.app.core.data.plant.characteristics

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.plant.characteristics.local.PlantCharacteristicsDAO
import plat.plantcare.app.core.data.plant.characteristics.remote.FirestorePlantCharacteristicsApi
import plat.plantcare.app.core.data.plant.characteristics.remote.PlantCharacteristicsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlantCharacteristicsModule {

    @Provides @Singleton
    fun providePlantCharacteristicsDao(database: AppDatabase): PlantCharacteristicsDAO =
        database.plantCharacteristicsDAO()

    @Provides @Singleton
    fun providePlantCharacteristicsApi(firestore: FirebaseFirestore): PlantCharacteristicsApi =
        FirestorePlantCharacteristicsApi(firestore)
}