package plat.plantcare.app.discover.data

data class PlantaRecomendada(
    val id: String,
    val imageUrl: String,
    val nombre: String,
    val nombreAlternativo: String,
    val caracteristica: String,
    val enListaDeseos: Boolean = false
)

data class DescubrirState(
    val plantasRecomendadas: List<PlantaRecomendada> = emptyList(),
    val busqueda: String = "",
    val soloDeseadas: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val pendingIds: Set<String> = emptySet()
) {
    val plantasFiltradas: List<PlantaRecomendada> get() = plantasRecomendadas.filter {
        (!soloDeseadas || it.enListaDeseos) &&
            (it.nombre.contains(busqueda.trim(), true) || it.nombreAlternativo.contains(busqueda.trim(), true))
    }
}
