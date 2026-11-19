package plat.plantcare.app.core.network

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @Provides @Singleton
    fun providePlantNetHttpClient(): HttpClient =
        HttpClientFactory.create(Service.PLANT_NET)

    @Provides @PlantNetApiKey
    fun providePlantNetApi(): String = Service.PLANT_NET.apiKey
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNetworkModule2 {

    @Binds @Singleton
    abstract fun bindNetworkMonitor(impl: AndroidNetworkMonitor): NetworkMonitor
}