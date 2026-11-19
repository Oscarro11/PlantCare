package plat.plantcare.app.core.data.plant.general.domain

import java.time.LocalDate

data class Plant(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val location: String,
    val birthDate: LocalDate?,        // Null when the entry date was never recorded
    val ownerId: String,
    val referenceId: String?,
    val favorite: Boolean = false
)

data class NewPlant(
    val name: String,
    val location: String,
    val imageUrl: String,
    val birthDate: LocalDate?
)

enum class PlantFormError {
    NAME_REQUIRED,
    NAME_TOO_LONG,
    LOCATION_TOO_LONG,
    DATE_IN_FUTURE
}

const val MAX_PLANT_NAME_LENGTH = 60
const val MAX_PLANT_LOCATION_LENGTH = 60

/** Empty set means valid. Use it for live field errors; the repository checks it again. */
fun NewPlant.validate(today: LocalDate = LocalDate.now()): Set<PlantFormError> = buildSet {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) add(PlantFormError.NAME_REQUIRED)
    if (trimmedName.length > MAX_PLANT_NAME_LENGTH) add(PlantFormError.NAME_TOO_LONG)
    if (location.trim().length > MAX_PLANT_LOCATION_LENGTH) add(PlantFormError.LOCATION_TOO_LONG)
    if (birthDate != null && birthDate.isAfter(today)) add(PlantFormError.DATE_IN_FUTURE)
}
/** A saved plant remains usable when optional AI care is unavailable. */
data class PlantSaveResult(val id: String, val careUnavailable: Boolean = false)
