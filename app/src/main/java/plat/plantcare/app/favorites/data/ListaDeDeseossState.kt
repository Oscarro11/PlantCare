package plat.plantcare.app.favorites.data

data class PlantaDeseada(val id: String, val imageUrl: String, val nombre: String, val nombreCientifico: String)
data class ListaDeseosState(
    val busqueda: String = "",
    val plantasDeseadas: List<PlantaDeseada> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val pendingIds: Set<String> = emptySet(),
    val ordenInverso: Boolean = false
) {
    val plantasFiltradas: List<PlantaDeseada> get() {
        val results = plantasDeseadas.filter {
            it.nombre.contains(busqueda.trim(), true) || it.nombreCientifico.contains(busqueda.trim(), true)
        }
        return if (ordenInverso) results.reversed() else results
    }
}
