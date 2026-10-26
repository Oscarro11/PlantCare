package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import plat.plantcare.app.R
import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantHumidity
import plat.plantcare.app.core.data.models.PlantRequiredLight
import plat.plantcare.app.core.data.models.PlantWateringFrequency
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

//Contains both Plant and PlantCharacteristics
interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
    fun observePlant(id: String): Flow<Plant?>
    fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?>
    fun observeDiagnostic(plantId: String): Flow<Diagnostic?>

    suspend fun addPlant(plant: Plant)
    suspend fun togglePlantFavorite(plantId: String)
}

@Singleton
class FakePlantRepository @Inject constructor() : PlantRepository {

    val fakePlants = MutableStateFlow(
        listOf(
            Plant(
                id = "plant-1",
                name = "Monstera",
                imageUrl = "https://placehold.co/600x400/png?text=Monstera",
                location = "Living room",
                birthDate = LocalDate.now().minusYears(2),
                ownerId = "user-1",
                referenceId = "ref-monstera"
            ),
            Plant(
                id = "plant-2",
                name = "Snake plant",
                imageUrl = "https://placehold.co/600x400/png?text=Snake_plant",
                location = "Bedroom",
                birthDate = LocalDate.now().minusMonths(8),
                ownerId = "user-1",
                referenceId = "ref-snake-plant"
            ),
            Plant(
                id = "plant-3",
                name = "Golden pothos",
                imageUrl = "https://placehold.co/600x400/png?text=Golden_pothos",
                location = "Kitchen",
                birthDate = null, // Entry date wasn't recorded
                ownerId = "user-1",
                referenceId = "ref-golden-pothos"
            )
        )
    )

    private val fakeCharacteristics = MutableStateFlow(
        listOf(
            PlantCharacteristics(
                plantId = "plant-1",
                requiredLight = PlantRequiredLight.BRILLANTE_INDIRECTA,
                wateringFrequencyDays = PlantWateringFrequency.MODERADO,
                humidityLevel = PlantHumidity.MEDIA,
                preferredClimate = "Cálido y húmedo",
                pot = "Maceta con orificios de drenaje",
                substrate = "Sustrato aireado para plantas tropicales"
            ),
            PlantCharacteristics(
                plantId = "plant-2",
                requiredLight = PlantRequiredLight.BAJA,
                wateringFrequencyDays = PlantWateringFrequency.ESPACIADO,
                humidityLevel = PlantHumidity.BAJA,
                preferredClimate = "Cálido y seco",
                pot = "Maceta de terracota con drenaje",
                substrate = "Sustrato arenoso y de buen drenaje"
            ),
            PlantCharacteristics(
                plantId = "plant-3",
                requiredLight = PlantRequiredLight.MEDIA_INDIRECTA,
                wateringFrequencyDays = PlantWateringFrequency.MODERADO,
                humidityLevel = PlantHumidity.MEDIA,
                preferredClimate = "Cálido y húmedo",
                pot = "Maceta con orificios de drenaje",
                substrate = "Sustrato universal ligero y bien drenado"
            )
        ).associateBy { it.plantId }
    )

    private val fakeDiagnostics = MutableStateFlow(
        listOf(
            Diagnostic(
                id = "diagnostic-1",
                plantId = "plant-1",
                date = LocalDate.now().minusDays(2),
                foliageState = "Healthy, deep green leaves",
                growthVigor = "Strong new growth",
                pestControl = "No pests observed",
                diseaseSigns = "No signs of disease",
                hasFlowersOrFruits = false,
                score = 92
            ),
            Diagnostic(
                id = "diagnostic-2",
                plantId = "plant-2",
                date = LocalDate.now().minusDays(5),
                foliageState = "Healthy leaves with slight dryness at the tips",
                growthVigor = "Steady growth",
                pestControl = "No pests observed",
                diseaseSigns = "No signs of disease",
                hasFlowersOrFruits = false,
                score = 78
            ),
            Diagnostic(
                id = "diagnostic-3",
                plantId = "plant-3",
                date = LocalDate.now().minusDays(1),
                foliageState = "Green leaves with minor yellowing",
                growthVigor = "Moderate growth",
                pestControl = "Check leaf undersides during routine care",
                diseaseSigns = "No clear signs of disease",
                hasFlowersOrFruits = false,
                score = 68
            )
        ).associateBy { it.plantId }
    )

    override fun observePlants(): Flow<List<Plant>> = fakePlants

    override fun observePlant(id: String): Flow<Plant?> =
        fakePlants
            .map { plants -> plants.firstOrNull { it.id == id } }
            .distinctUntilChanged()

    override fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?> =
        fakeCharacteristics
            .map { it[plantId] }
            .distinctUntilChanged()

    override fun observeDiagnostic(plantId: String): Flow<Diagnostic?> =
        fakeDiagnostics
            .map { it[plantId] }
            .distinctUntilChanged()

    override suspend fun addPlant(plant: Plant) {
        val newPlant = if (plant.id.isBlank()) {
            plant.copy(id = UUID.randomUUID().toString())
        } else plant

        fakePlants.update { current -> current + newPlant }
    }

    override suspend fun togglePlantFavorite(plantId: String) {
        fakePlants.update { plants ->
            plants.map { plant ->
                if (plant.id == plantId) {
                    plant.copy(favorite = !plant.favorite)
                } else {
                    plant
                }
            }
        }
    }
}