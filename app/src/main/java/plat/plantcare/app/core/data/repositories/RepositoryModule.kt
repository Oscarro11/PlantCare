package plat.plantcare.app.core.data.repositories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(impl: FakeTaskRepository): TaskRepository

    @Binds
    abstract fun bindPlantRepository(impl: FakePlantRepository): PlantRepository

    @Binds
    abstract fun bindPlantReferenceRepository(impl: FakePlantReferenceRepository): PlantReferenceRepository

    @Binds
    abstract fun bindUserRepository(impl: FakeUserRepository): UserRepository

    @Binds
    abstract fun bindWeatherRepository(impl: FakeWeatherRepository): WeatherRepository
}