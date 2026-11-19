package plat.plantcare.app.collection

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.collection.allPlants.data.*
import plat.plantcare.app.collection.allPlants.ui.CollectionScreen
import plat.plantcare.app.ui.theme.PlantcareTheme
import java.io.File

@RunWith(AndroidJUnit4::class)
class CollectionUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun darkCollectionCanSwitchLayoutAndOpenTheWholeCard() = verify(true)
    @Test fun lightCollectionCanSwitchLayoutAndOpenTheWholeCard() = verify(false)

    private fun verify(dark: Boolean) {
        var opened = ""
        compose.setContent {
            var state by remember { mutableStateOf(CollectionState(totalPlants = 2, loading = false,
                healthPercentage = 1f, plantList = listOf(
                    CollectionCardInfo("monstera", "Monstera de la sala", null, "Sala"),
                    CollectionCardInfo("fern", "Helecho", null, "Balcón")))) }
            PlantcareTheme(darkTheme = dark) {
                CollectionScreen({}, { opened = it }, { state = state.copy(pendingOnly = !state.pendingOnly) },
                    { state = state.copy(searchQuery = it) },
                    { state = state.copy(selectedLayout = if (state.selectedLayout == CollectionLayout.GRID) CollectionLayout.LIST else CollectionLayout.GRID) }, state)
            }
        }
        compose.onNodeWithText("Monstera de la sala").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("monstera", opened) }
        compose.onNodeWithContentDescription("Vista de lista").performScrollTo().performClick()
        compose.onNodeWithText("Helecho").performScrollTo().assertIsDisplayed()
        compose.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            File(context.getExternalFilesDir(null), if (dark) "collection-dark.png" else "collection-light.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
