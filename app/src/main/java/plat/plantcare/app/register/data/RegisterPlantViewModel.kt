package plat.plantcare.app.register.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import plat.plantcare.app.core.data.plant.identification.PlantIdentificationApi
import plat.plantcare.app.core.data.plant.identification.IdentificationError
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.general.domain.NewPlant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val identificationApi: PlantIdentificationApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var registrationId = UUID.randomUUID().toString()
    private val _state = MutableStateFlow(RegisterPlantState())
    val state = _state.asStateFlow()

    fun handleEvent(event: RegisterPlantEvent) {
        when (event) {
            is RegisterPlantEvent.ChangeDraft -> changeDraft(event.draft)
            RegisterPlantEvent.IdentifyPhoto -> identifyPhoto()
            is RegisterPlantEvent.SelectSpecies -> selectSpecies(event.scientificName)
            RegisterPlantEvent.Save -> savePlant()
            RegisterPlantEvent.Discard -> if (!_state.value.isSaving) {
                registrationId = UUID.randomUUID().toString()
                _state.value = RegisterPlantState()
            }
        }
    }

    private fun changeDraft(draft: RegisterPlantDraft) {
        if (_state.value.isSaving || _state.value.saved) return
        val changedPhoto = draft.imageUrl != _state.value.draft.imageUrl
        _state.update { it.copy(draft = if (changedPhoto) draft.copy(identifiedSpecies = null) else draft,
            identificationResults = if (changedPhoto) emptyList() else it.identificationResults,
            identificationError = null, error = null, saved = false) }
    }

    private fun selectSpecies(scientificName: String) {
        if (_state.value.identifying) return
        if (_state.value.identificationResults.none { it.scientificName == scientificName }) return
        _state.update { it.copy(draft = it.draft.copy(identifiedSpecies = scientificName)) }
    }

    private fun identifyPhoto() {
        val imageUrl = _state.value.draft.imageUrl ?: return
        if (_state.value.identifying || _state.value.isSaving) return
        _state.update { it.copy(identifying = true, identificationError = null, identificationResults = emptyList()) }
        viewModelScope.launch {
            try {
                val photo = withContext(Dispatchers.IO) { photoAsJpeg(imageUrl) }
                val results = identificationApi.identify(photo)
                _state.update { it.copy(identifying = false, identificationResults = results,
                    identificationError = if (results.isEmpty()) "No se identificó la especie. Puedes continuar manualmente." else null) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                val message = when (error) {
                    is IdentificationError.InvalidApiKey -> "La clave de PlantNet no está autorizada."
                    is IdentificationError.QuotaExceeded -> "Se alcanzó el límite de consultas de PlantNet."
                    is IdentificationError.NoNetwork -> "No hay conexión. Revisa Internet."
                    else -> "No se pudo identificar la planta. Puedes continuar sin identificarla."
                }
                _state.update { it.copy(identifying = false, identificationError = message) }
            }
        }
    }

    private fun photoAsJpeg(imageUrl: String): ByteArray {
        val uri = Uri.parse(imageUrl)
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("No se pudo abrir la fotografía")
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (bounds.outWidth / sample > 1600 || bounds.outHeight / sample > 1600) sample *= 2
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size,
            BitmapFactory.Options().apply { inSampleSize = sample })
            ?: throw IllegalArgumentException("Formato de imagen no compatible")
        val orientation = context.contentResolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val corrected = if (degrees != 0f) Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
            Matrix().apply { postRotate(degrees) }, true) else bitmap
        return ByteArrayOutputStream().use { output ->
            corrected.compress(Bitmap.CompressFormat.JPEG, 85, output)
            output.toByteArray()
        }
    }

    private fun savePlant() {
        if (_state.value.isSaving || _state.value.saved) return
        val draft = _state.value.draft

        if (!draft.isValid) {
            _state.update { it.copy(error = "Ingresa el nombre de la planta") }
            return
        }

        _state.update { it.copy(isSaving = true, saved = false, error = null) }

        viewModelScope.launch {
            try {
                val result = plantRepository.addPlant(
                    plantId = registrationId,
                    newPlant = NewPlant(
                        name = draft.plantName,
                        imageUrl = draft.imageUrl.orEmpty(),
                        location = draft.location,
                        birthDate = draft.arrivalDate
                    ),
                    characteristics = PlantCharacteristics(
                        plantId = "",
                        lightExposure = draft.lightExposure,
                        wateringFrequencyDays = draft.wateringFrequency,
                        waterVolume = draft.waterVolume,
                        humidityLevel = draft.humidity,
                        currentClimate = draft.currentClimate,
                        potSize = draft.potSize,
                        potType = draft.potType,
                        substrate = draft.substrate,
                        attentionLevel = draft.attentionLevel
                    )
                )

                _state.update { it.copy(isSaving = false, saved = true, error = null,
                    warning = if (result.careUnavailable) "Planta guardada. Las pautas de cuidado con IA no están disponibles por ahora." else null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        saved = false,
                        error = "No se pudo confirmar el guardado. Revisa la conexión y vuelve a intentarlo; no se duplicará la planta."
                    )
                }
            }
        }
    }
}
