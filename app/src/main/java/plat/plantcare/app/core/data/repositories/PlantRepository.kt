package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import plat.plantcare.app.core.data.models.Plant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
}

@Singleton
class FakePlantRepository @Inject constructor() : PlantRepository {

    val fakePlants = listOf(
        Plant(
            id = "plant-1",
            name = "Monstera",
            imageUrl = "https://placehold.co/600x600?text=Monstera",
            location = "Living room",
            birthDate = LocalDate.now().minusYears(2),
            ownerId = "user-1",
            referenceId = "ref-monstera"
        ),
        Plant(
            id = "plant-2",
            name = "Snake plant",
            imageUrl = "https://placehold.co/600x600?text=Snake+plant",
            location = "Bedroom",
            birthDate = LocalDate.now().minusMonths(8),
            ownerId = "user-1",
            referenceId = "ref-snake-plant"
        ),
        Plant(
            id = "plant-3",
            name = "Golden pothos",
            imageUrl = "https://placehold.co/600x600?text=Pothos",
            location = "Kitchen",
            birthDate = null, // Entry date wasn't recorded
            ownerId = "user-1",
            referenceId = "ref-golden-pothos"
        ),
        Plant(
            id = "plant-4",
            name = "Fiddle-leaf fig",
            imageUrl = "https://placehold.co/600x600?text=Fiddle-leaf+fig",
            location = "Near the window",
            birthDate = LocalDate.now().minusDays(45),
            ownerId = "user-1",
            referenceId = "ref-fiddle-leaf-fig"
        )
    )

    override fun observePlants(): Flow<List<Plant>> = flowOf(fakePlants)
}