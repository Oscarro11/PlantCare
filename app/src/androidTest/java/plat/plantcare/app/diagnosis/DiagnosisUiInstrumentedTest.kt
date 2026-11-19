package plat.plantcare.app.diagnosis

import plat.plantcare.app.diagnosis.domain.DiagnosisAnswer
import plat.plantcare.app.diagnosis.ui.*

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.collection.plantDetails.ui.DiagnosticSection
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.ui.theme.PlantcareTheme

@RunWith(AndroidJUnit4::class)
class DiagnosisUiInstrumentedTest {
    @get:Rule val compose = createComposeRule()

    @Test fun firstDiagnosisRemainsAccessibleWithoutAnyPreviousResult() {
        var opened = false
        compose.setContent {
            PlantcareTheme {
                DiagnosticSection("Sin diagnóstico", null, onCreate = { opened = true })
            }
        }
        compose.onNodeWithText("Diagnóstico", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Nuevo diagnóstico").assertIsDisplayed().performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun largeTextResultCanBeSavedWithoutTriggeringAnotherAnalysis() {
        var saves = 0
        var analyses = 0
        val plant = Plant("plant-ui", "Monstera de prueba", null, "Sala", null, "test-user", null)
        val result = DiagnosisAnswer(true, true, "NO_EVALUABLE", "Resultado de interfaz",
            "Hojas visibles", "No evaluable en una foto", "No concluyente", "No concluyente", null,
            listOf("Posibilidad por comprobar"), listOf("Comprobar el sustrato"), listOf("Raíces fuera de la imagen"))
            .toDiagnostic(plant.id, "ui-test")
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale = 1.3f)) {
                PlantcareTheme(darkTheme = true) {
                    DiagnosisScreen(
                        state = DiagnosisState(plants = listOf(plant), loadingPlants = false, selectedId = plant.id, result = result),
                        fixedPlant = true, onBack = {}, onAddPlant = {}, onSelectPlant = {}, onPhoto = {},
                        onAnalyze = { analyses++ }, onSave = { saves++ }, onRetry = {}, onCameraError = {}
                    )
                }
            }
        }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Guardar diagnóstico"))
        compose.onNodeWithText("Guardar diagnóstico").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, saves); assertEquals(0, analyses) }
    }
}
