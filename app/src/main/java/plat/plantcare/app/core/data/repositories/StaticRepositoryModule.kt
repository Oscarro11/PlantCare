package plat.plantcare.app.core.data.repositories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StaticRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: FakeTaskRepository): TaskRepository

    @Binds
    @Singleton
    abstract fun bindPlantRepository(impl: FakePlantRepository): PlantRepository

    @Binds
    @Singleton
    abstract fun bindPlantReferenceRepository(impl: FakePlantReferenceRepository): PlantReferenceRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FakeUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: FakeWeatherRepository): WeatherRepository
}