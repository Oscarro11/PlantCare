package plat.plantcare.app.discover.data

data class PlantaDetalle(
    val imageUrl: String = "",
    val nombre: String = "",
    val nombreCientifico: String = "",
    val familia: String = "Sin información",
    val origen: String = "Sin información",
    val descripcion: String = ""
)
data class CaracteristicaPlanta(val icono: String, val titulo: String, val valor: String, val descripcion: String = "", val advertencia: Boolean = false)
data class PasoCuidado(val titulo: String, val descripcion: String)
data class FichaBotanicaState(
    val planta: PlantaDetalle = PlantaDetalle(),
    val caracteristicas: List<CaracteristicaPlanta> = emptyList(),
    val pasos: List<PasoCuidado> = emptyList(),
    val enListaDeseos: Boolean = false,
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
