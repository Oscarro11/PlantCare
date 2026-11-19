package plat.plantcare.app.diagnosis.data.photos

import plat.plantcare.app.core.photos.local.LocalImageStorage
import plat.plantcare.app.diagnosis.domain.DiagnosisPhotos
import javax.inject.Inject

class LocalDiagnosisPhotos @Inject constructor(private val storage: LocalImageStorage) : DiagnosisPhotos {
    override suspend fun importPhoto(source: String) = storage.importImage(source)
    override suspend fun read(photo: String) = storage.readForUpload(photo)
    override suspend fun delete(photo: String) = storage.delete(photo)
}
