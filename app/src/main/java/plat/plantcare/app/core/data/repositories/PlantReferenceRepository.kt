package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import plat.plantcare.app.core.data.models.PlantReference
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface PlantReferenceRepository {
    fun observePlantReferences(): Flow<List<PlantReference>>
    fun observePlantReference(id: String): Flow<PlantReference?>
    fun observePlantConsiderations(referenceId: String): Flow<PlantReferenceConsiderations?>
    suspend fun addPlantReference(reference: PlantReference)
}

@Singleton
class FakePlantReferenceRepository @Inject constructor() : PlantReferenceRepository {

    val fakeReferences = MutableStateFlow(
        listOf(
            PlantReference(
                id = "ref-monstera",
                scientificName = "Monstera deliciosa",
                description = "Planta de casa :)"
            ),
            PlantReference(
                id = "ref-snake-plant",
                scientificName = "Zea mays",
                description = "Planta de jardin :("
            ),
            PlantReference(
                id = "ref-golden-pothos",
                scientificName = "Phaseolus vulgaris",
                description = "Planta de casa :)"
            )
        )
    )

    private val fakeReferenceConsiderations = MutableStateFlow(
        listOf(
            PlantReferenceConsiderations(
                referenceId = "ref-monstera",
                wateringGuide = "Riega cuando los primeros 2-3 cm del sustrato estén secos; evita dejar agua acumulada en el plato.",
                recommendedLocationAndLight = "Colócala en un lugar cálido con luz brillante indirecta; evita el sol fuerte directo.",
                nutrition = "Aplica fertilizante equilibrado cada 4 semanas durante primavera y verano.",
                sprayingAndCleaning = "Limpia las hojas con un paño húmedo. Agradece la humedad ambiental, pero no necesita pulverización diaria."
            ),
            PlantReferenceConsiderations(
                referenceId = "ref-snake-plant",
                wateringGuide = "Deja que el sustrato se seque por completo antes de volver a regar; reduce el riego en época fría.",
                recommendedLocationAndLight = "Tolera poca luz, pero crece mejor con luz indirecta brillante. Evita el sol intenso y el frío.",
                nutrition = "Fertiliza ligeramente cada 6-8 semanas en primavera y verano; no abones durante el invierno.",
                sprayingAndCleaning = "Retira el polvo de las hojas con un paño húmedo. No requiere pulverización."
            ),
            PlantReferenceConsiderations(
                referenceId = "ref-golden-pothos",
                wateringGuide = "Riega cuando la capa superior del sustrato esté seca y desecha el exceso de agua.",
                recommendedLocationAndLight = "Ubícala con luz indirecta media o brillante; evita el sol directo prolongado.",
                nutrition = "Aplica fertilizante equilibrado cada 4-6 semanas en primavera y verano.",
                sprayingAndCleaning = "Limpia las hojas con un paño húmedo. Puedes pulverizar ocasionalmente si el ambiente está muy seco."
            )
        ).associateBy { it.referenceId }
    )

    override fun observePlantReferences(): Flow<List<PlantReference>> = fakeReferences

    override fun observePlantReference(id: String): Flow<PlantReference?> =
        fakeReferences
            .map { references -> references.firstOrNull { it.id == id } }
            .distinctUntilChanged()

    override fun observePlantConsiderations(referenceId: String): Flow<PlantReferenceConsiderations?> =
        fakeReferenceConsiderations
            .map { it[referenceId] }
            .distinctUntilChanged()

    override suspend fun addPlantReference(reference: PlantReference) {
        val newReference = if (reference.id.isBlank()) {
            reference.copy(id = UUID.randomUUID().toString())
        } else reference

        fakeReferences.update { current -> current + newReference }
    }
}