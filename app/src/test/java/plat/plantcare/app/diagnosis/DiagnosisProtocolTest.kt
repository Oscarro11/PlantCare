package plat.plantcare.app.diagnosis

import plat.plantcare.app.diagnosis.domain.DiagnosisAnswer
import plat.plantcare.app.diagnosis.data.gemini.GeminiDiagnosisResponse

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.diagnosis.domain.HealthStatus
import plat.plantcare.app.core.data.models.PlantHumidity
import plat.plantcare.app.core.data.models.PlantRequiredLight
import plat.plantcare.app.core.data.models.PlantWateringFrequency
import plat.plantcare.app.diagnosis.domain.getHealthStatus
import plat.plantcare.app.core.data.models.toFirebaseDTO
import plat.plantcare.app.core.data.remote.Firebase.PlantCharacteristicsFirebaseDTO
import plat.plantcare.app.core.data.remote.Firebase.toModel

class DiagnosisProtocolTest {
    private fun answer() = DiagnosisAnswer(true, true, "NO_EVALUABLE", "Observación limitada",
        "Algunas hojas visibles", "No se puede determinar el crecimiento con una foto",
        "No se distinguen plagas", "No hay evidencia suficiente", null, emptyList(),
        listOf("Revisar el sustrato antes de regar"), listOf("La foto no muestra las raíces"))
    private fun jsonAnswer() = GeminiDiagnosisResponse(true, true, "NO_EVALUABLE", "Observación limitada",
        "Algunas hojas visibles", "No se puede determinar el crecimiento con una foto",
        "No se distinguen plagas", "No hay evidencia suficiente", null, emptyList(),
        listOf("Revisar el sustrato antes de regar"), listOf("La foto no muestra las raíces"))

    @Test fun validResponseKeepsUncertaintyWithoutInventingHealthScore() {
        val result = GeminiDiagnosisResponse.parse(Json.encodeToString(jsonAnswer())).toDiagnostic("plant-1", "test-model")
        assertNull(result.score)
        assertNull(result.hasFlowersOrFruits)
        assertEquals(HealthStatus.NO_EVALUABLE, result.getHealthStatus())
        assertEquals("plant-1", result.plantId)
    }
    @Test fun nonPlantAndBlurryPhotosAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { answer().copy(isPlant = false).validate() }
        assertThrows(IllegalArgumentException::class.java) { answer().copy(imageUsable = false).validate() }
    }
    @Test fun invalidStatusAndMissingRecommendationsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { answer().copy(assessment = "MUERTA").validate() }
        assertThrows(IllegalArgumentException::class.java) { answer().copy(recommendations = emptyList()).validate() }
        assertThrows(IllegalArgumentException::class.java) { answer().copy(limitations = emptyList()).validate() }
    }
    @Test fun absentCharacteristicsStayUnknown() {
        val model = PlantCharacteristicsFirebaseDTO().toModel()
        assertEquals(PlantRequiredLight.NO_REGISTRADO, model.requiredLight)
        assertEquals(PlantWateringFrequency.NO_REGISTRADO, model.wateringFrequencyDays)
        assertEquals(PlantHumidity.NO_REGISTRADO, model.humidityLevel)
        assertNull(model.wateringDays)
        assertNull(model.humidityPercent)
    }
    @Test fun lightComesFromLightFieldAndNumericValuesRoundTrip() {
        val model = PlantCharacteristicsFirebaseDTO(requiredLight = "Brillante indirecta", preferredClimate = "Tropical",
            wateringFrequencyDays = 7, humidityLevel = 55).toModel()
        assertEquals(PlantRequiredLight.BRILLANTE_INDIRECTA, model.requiredLight)
        assertEquals(7, model.toFirebaseDTO().wateringFrequencyDays)
        assertEquals(55, model.toFirebaseDTO().humidityLevel)
    }
}
