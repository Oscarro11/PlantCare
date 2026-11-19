package plat.plantcare.app.diagnosis.domain

import plat.plantcare.app.core.data.plant.general.domain.Plant

/** Registered requirements, not measurements of the plant's current environment. */
data class DiagnosisRequirements(
    val requiredLight: String,
    val wateringFrequencyDays: Int?,
    val humidityLevel: Int?,
    val preferredClimate: String,
    val pot: String,
    val substrate: String,
    val wateringRange: String? = null,
    val humidityRange: String? = null
)

data class DiagnosisContext(
    val uid: String,
    val plant: Plant,
    val characteristics: DiagnosisRequirements?,
    val scientificName: String?
)

data class DiagnosisDetails(
    val characteristics: DiagnosisRequirements? = null,
    val latest: Diagnostic? = null
)

data class AnalyzedPlant(val answer: DiagnosisAnswer, val model: String)
data class DiagnosisDraft(val context: DiagnosisContext, val diagnostic: Diagnostic)
