package plat.plantcare.app.integration

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import plat.plantcare.app.BuildConfig
import plat.plantcare.app.R
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.plant.PlantRepositoryImpl
import plat.plantcare.app.core.data.plant.characteristics.remote.*
import plat.plantcare.app.core.data.plant.characteristics.local.toEntity
import plat.plantcare.app.core.data.plant.considerations.remote.*
import plat.plantcare.app.core.data.plant.considerations.local.toEntity
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.plant.general.local.toEntity
import plat.plantcare.app.core.data.plant.general.remote.*
import plat.plantcare.app.core.data.plant.identification.*
import plat.plantcare.app.core.data.tasks.*
import plat.plantcare.app.core.network.NetworkMonitor
import plat.plantcare.app.core.photos.local.DeviceImageStorage
import plat.plantcare.app.core.photos.remote.*
import plat.plantcare.app.notifications.*
import java.time.LocalDate
import java.util.UUID

/** Production repositories and real local Firebase services; the AI response is controlled explicitly. */
class FinalFeaturesInstrumentedTest {
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private val authRepo get()=EntryPointAccessors.fromApplication(context,TaskWorkerEntryPoint::class.java).auth()
    private val remote get()=FirebaseFirestore.getInstance()
    private suspend fun account(block: suspend (String)->Unit) {
        assumeTrue("Use the local Firebase build",BuildConfig.USE_EMULATOR)
        authRepo.signOut()
        val email="final.qa.${UUID.randomUUID()}@example.com"
        val password="PlantCareFinalQA2026"
        authRepo.registerWithEmail(email,password,"Daniel QA","Guatemala, Guatemala").getOrThrow()
        val uid=requireNotNull(authRepo.currentUid)
        try { withTimeout(60_000) { block(uid) } }
        finally { withContext(NonCancellable) { withTimeout(20_000) {
            val root=remote.collection("usuarios").document(uid)
            for (plant in root.collection("plantas").get(Source.SERVER).await()) {
                for (name in listOf("caracteristicas","consideraciones","diagnosticos"))
                    for (child in plant.reference.collection(name).get().await()) child.reference.delete().await()
                plant.reference.delete().await()
            }
            for (name in listOf("tareas","caracteristicas"))
                for (child in root.collection(name).get().await()) child.reference.delete().await()
            root.delete().await()
            FirebaseAuth.getInstance().currentUser?.let {
                it.reauthenticate(EmailAuthProvider.getCredential(email,password)).await(); it.delete().await()
            }
            authRepo.signOut()
        } } }
    }
    @Test fun customProfilePersistsAndCanAuthenticateAgain() = runBlocking {
        account { uid ->
            val profile=remote.document("usuarios/$uid").get(Source.SERVER).await()
            assertEquals("Daniel QA",profile.getString("name"))
            assertEquals("Guatemala, Guatemala",profile.getString("location"))
            val email=requireNotNull(FirebaseAuth.getInstance().currentUser?.email)
            authRepo.signOut()
            authRepo.signInWithEmail(email,"PlantCareFinalQA2026").getOrThrow()
            assertEquals(uid,authRepo.currentUid)
            assertEquals(uid,authRepo.sessionUid.first())
            assertTrue(authRepo.currentUser.first()?.uid==uid)
        }
    }
    @Test fun regenerationPersistsAndFailureKeepsPreviousGuidesThenDeletionCleansEverything() = runBlocking {
        account { uid ->
            val db=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).build()
            val id="final-"+UUID.randomUUID()
            val path="usuarios/$uid/plantas/$id.jpg"
            val photos=FirebasePhotoStorage(FirebaseStorage.getInstance())
            val bytes=context.resources.openRawResource(R.drawable.plant_img_example).use { source ->
                val bitmap=android.graphics.BitmapFactory.decodeStream(source)
                java.io.ByteArrayOutputStream().use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG,85,out)
                    bitmap.recycle(); out.toByteArray()
                }
            }
            val url=photos.upload(path,bytes)
            try {
                val plant=Plant(id,"Monstera QA",url,"Sala",null,uid,null)
                val api=FirestorePlantApi(remote)
                val details=PlantCharacteristicsDTO(plantId=id)
                api.create(uid,plant.toCommonDTO(),details)
                db.plantDAO().updateOrInsertPlant(plant.toEntity())
                db.plantCharacteristicsDAO().updateOrInsertPlantCharacteristics(details.toDomain().toEntity())
                val care=FirestorePlantConsiderationsApi(remote)
                val old=PlantConsiderationsDTO(id,"Monstera deliciosa","Anterior","Luz","Nutrición","Limpieza")
                care.addToPlant(uid,id,old)
                db.plantConsiderationsDAO().updateOrInsertPlantConsiderations(old.toDomain().toEntity())
                var fail=false
                val ai=object: PlantConsiderationsAiApi {
                    override suspend fun generateConsiderations(speciesName: String?,photo: ByteArray): PlantConsiderationsAiDTO {
                        if (fail) error("Provider unavailable for this test")
                        assertTrue(photo.isNotEmpty()); assertEquals("Monstera deliciosa",speciesName)
                        return PlantConsiderationsAiDTO("Nuevo riego","Nueva luz","Nueva nutrición","Nueva limpieza")
                    }
                }
                val identity=object: PlantIdentificationApi {
                    override suspend fun identify(photo: ByteArray,language: String)=emptyList<PlantNetResponseDTO>()
                }
                var online=true
                val network=object: NetworkMonitor { override fun isOnline()=online }
                val store=LocalTaskStore(db)
                val tasks=SyncedTaskRepository(store,remote,authRepo,TaskWorkScheduler(context))
                val repo=PlantRepositoryImpl(authRepo,network,photos,DeviceImageStorage(context),api,
                    FirestorePlantCharacteristicsApi(remote),care,db.plantDAO(),db.plantCharacteristicsDAO(),
                    db.plantConsiderationsDAO(),ai,identity,db,tasks)
                repo.regenerateConsiderations(id)
                assertEquals("Nuevo riego",care.getCharacteristics(uid,id).wateringGuide)
                assertEquals("Monstera deliciosa",care.getCharacteristics(uid,id).scientificName)
                assertEquals("Nuevo riego",db.plantConsiderationsDAO().observePlantConsiderations(id).first()?.wateringGuide)
                fail=true
                assertTrue(runCatching { repo.regenerateConsiderations(id) }.isFailure)
                assertEquals("Nuevo riego",db.plantConsiderationsDAO().observePlantConsiderations(id).first()?.wateringGuide)
                val task=Task("care-$id",id,LocalDate.now(),TaskAction.WATER,"Riego",false,null,7)
                store.create(uid,task); tasks.syncPending(uid)
                store.complete(uid,task.id,LocalDate.now()) // Pending local completion must never recreate the deleted plant's care.
                remote.document("usuarios/$uid/plantas/$id/diagnosticos/legacy").set(mapOf("plantId" to id,"date" to com.google.firebase.Timestamp.now(),
                    "assessment" to "NO_EVALUABLE","summary" to "Prueba de borrado","foliageState" to "Prueba",
                    "growthVigor" to "Prueba","pestControl" to "Prueba","diseaseSigns" to "Prueba",
                    "model" to "controlled-test","recommendations" to listOf("Revisar"),
                    "possibleCauses" to emptyList<String>(),"limitations" to listOf("Prueba"))).await()
                online=false
                assertTrue(runCatching { repo.deletePlant(id) }.isFailure)
                assertNotNull(db.plantDAO().observePlant(id).first())
                online=true
                repo.deletePlant(id)
                tasks.syncPending(uid)
                assertNull(db.plantDAO().observePlant(id).first())
                assertNull(db.plantCharacteristicsDAO().observePlantCharacteristics(id).first())
                assertNull(db.plantConsiderationsDAO().observePlantConsiderations(id).first())
                assertTrue(store.dao.observe(uid).first().isEmpty())
                assertTrue(store.dao.writes(uid).isEmpty())
                assertFalse(remote.document("usuarios/$uid/plantas/$id").get(Source.SERVER).await().exists())
                for (name in listOf("caracteristicas","consideraciones","diagnosticos"))
                    assertTrue(remote.collection("usuarios/$uid/plantas/$id/$name").get(Source.SERVER).await().isEmpty)
                assertTrue(remote.collection("usuarios/$uid/tareas").get(Source.SERVER).await().isEmpty)
                val missing=runCatching { FirebaseStorage.getInstance().reference.child(path).metadata.await() }.exceptionOrNull()
                assertEquals(StorageException.ERROR_OBJECT_NOT_FOUND,(missing as? StorageException)?.errorCode)
            } finally { runCatching { photos.delete(url) }; db.close() }
        }
    }
}
