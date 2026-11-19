package plat.plantcare.app.core.data.plant.identification

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import plat.plantcare.app.core.data.plant.general.domain.Plant
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlantIdentificationModule {

    @Binds @Singleton
    abstract fun providePlantIdentificationApi(impl: PlantNetIdentificationApi): PlantIdentificationApi
}