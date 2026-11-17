package plat.plantcare.app.core.data.repositories

import plat.plantcare.app.core.data.tasks.SyncedTaskRepository
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
    abstract fun bindTaskRepository(impl: SyncedTaskRepository): TaskRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: FakeWeatherRepository): WeatherRepository
}