package plat.plantcare.app.core.data.plant.general

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlantRepositoryModule {

    @Binds @Singleton
    abstract fun bindPlantRepository(impl: PlantRepositoryImpl): PlantRepository
}