package plat.plantcare.app.integration

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import plat.plantcare.app.R
import plat.plantcare.app.core.data.plant.identification.PlantNetIdentificationApi
import plat.plantcare.app.core.network.HttpClientFactory
import plat.plantcare.app.core.network.Service
import java.io.ByteArrayOutputStream
import java.io.File
import org.json.JSONObject

/** Explicitly opted in: uploads only the example plant bundled with the app. */
class PlantNetLiveInstrumentedTest {
    @Test fun ktorIdentifiesTheBundledPhotoWithTheRealService() = runBlocking {
        assumeTrue(InstrumentationRegistry.getArguments().getString("testPlantNet") == "true")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val photo = context.resources.openRawResource(R.drawable.plant_img_example).use { source ->
            val bitmap = BitmapFactory.decodeStream(source)
            ByteArrayOutputStream().use { bytes ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bytes)
                bitmap.recycle()
                bytes.toByteArray()
            }
        }
        HttpClientFactory.create(Service.PLANT_NET).use { client ->
            val results = withTimeout(55_000) {
                PlantNetIdentificationApi(client, Service.PLANT_NET.apiKey).identify(photo)
            }
            assertTrue("The bundled example must have at least one candidate", results.isNotEmpty())
            val best = results.maxBy { it.score }
            assertTrue(best.scientificName.isNotBlank())
            assertTrue(best.score in 0f..1f)
            File(context.getExternalFilesDir(null), "plantnet-live-result.json").writeText(
                JSONObject().put("scientificName",best.scientificName).put("score",best.score.toDouble())
                    .put("candidateCount",results.size).toString())
        }
    }
}
