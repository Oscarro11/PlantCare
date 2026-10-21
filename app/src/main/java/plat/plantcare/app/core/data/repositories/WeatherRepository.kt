package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface WeatherRepository {
    fun getTodayClimate(): Flow<String>
}

@Singleton
class FakeWeatherRepository @Inject constructor() : WeatherRepository {
    override fun getTodayClimate(): Flow<String> = flowOf(
        "30° C"
    )
}