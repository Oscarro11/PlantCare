package plat.plantcare.app.core.data.plant.considerations

import com.google.firebase.ai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.plant.considerations.local.PlantConsiderationsDAO
import plat.plantcare.app.core.data.plant.considerations.remote.FirestorePlantConsiderationsApi
import plat.plantcare.app.core.data.plant.considerations.remote.GeminiPlantConsiderationsApi
import plat.plantcare.app.core.data.plant.considerations.remote.PlantConsiderationsAiApi
import plat.plantcare.app.core.data.plant.considerations.remote.PlantConsiderationsStorageApi
import plat.plantcare.app.core.data.plant.considerations.remote.considerationsSchema
import plat.plantcare.app.core.network.AiModelFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantConsiderationsModel

@Module
@InstallIn(SingletonComponent::class)
object PlantConsiderationsModule {

    @Provides @Singleton
    fun providePlantConsiderationsDao(database: AppDatabase): PlantConsiderationsDAO =
        database.plantConsiderationsDAO()

    @Provides @Singleton
    fun providePlantConsiderationsStorageApi(firestore: FirebaseFirestore): PlantConsiderationsStorageApi =
        FirestorePlantConsiderationsApi(firestore)
    
    @Provides @Singleton @PlantConsiderationsModel
    fun providePlantConsiderationsModel(factory: AiModelFactory): GenerativeModel =
        factory.create(
            schema = considerationsSchema,
            instructions = "Eres un experto en botánica. Recibes la imagen de una planta junto con" +
                    " su especie, y respondes solo con las consideraciones de cuidado" +
                    " que le corresponden, en español"
        )

    @Provides @Singleton
    fun providePlantConsiderationsAiApi(@PlantConsiderationsModel model: GenerativeModel): PlantConsiderationsAiApi =
        GeminiPlantConsiderationsApi(model)
}

