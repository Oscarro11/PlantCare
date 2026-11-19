package plat.plantcare.app.core.data.plant.general

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.plant.general.local.PlantDAO
import plat.plantcare.app.core.data.plant.general.remote.FirestorePlantApi
import plat.plantcare.app.core.data.plant.general.remote.PlantDataSourceApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlantDataModule {

    @Provides @Singleton
    fun providePlantDao(database: AppDatabase): PlantDAO =
        database.plantDAO()

    @Provides @Singleton
    fun providePlantApi(firestore: FirebaseFirestore): PlantDataSourceApi =
        FirestorePlantApi(firestore)
}