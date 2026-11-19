package plat.plantcare.app.diagnosis

import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.BuildConfig
import plat.plantcare.app.core.ai.firebase.AiProjectConfiguration
import plat.plantcare.app.core.ai.firebase.FirebaseAiApp

class FirebaseAiAppInstrumentedTest {
    @Test fun selectedProjectNeverChangesPrimaryAuthAndDataConfiguration() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val primary = FirebaseApp.getInstance()
        val before = primary.options
        val selected = AiProjectConfiguration.options(context)
        val ai = FirebaseAiApp.get(context)
        val expectedId = if (BuildConfig.AI_PROJECT == "plantcare") before.projectId else "plantcare-ia"
        assertEquals(expectedId, ai.options.projectId)
        assertEquals(selected, ai.options)
        assertNotSame(primary, ai)
        assertSame(primary, FirebaseApp.getInstance())
        assertEquals(before, FirebaseApp.getInstance().options)
        assertEquals("plantcare-3a58c", primary.options.projectId)
        assertSame(ai, FirebaseAiApp.get(context))
    }

    @Test fun bothConfigurationsAreAvailableAndUnknownSelectionIsRejected() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val main = AiProjectConfiguration.options(context, "plantcare")
        val spark = AiProjectConfiguration.options(context, "plantcare-ia")
        assertEquals(FirebaseApp.getInstance().options, main)
        assertEquals("plantcare-ia", spark.projectId)
        assertNotEquals(main.applicationId, spark.applicationId)
        assertTrue(spark.apiKey.isNotBlank())
        assertThrows(IllegalArgumentException::class.java) { AiProjectConfiguration.options(context, "unknown") }
    }
}
