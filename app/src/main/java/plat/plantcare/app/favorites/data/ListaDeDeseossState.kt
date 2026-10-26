package plat.plantcare.app.favorites.data

import androidx.annotation.DrawableRes
import plat.plantcare.app.R

data class PlantaDeseada(
    val id: String,
    @DrawableRes val imagen: Int,
    val nombre: String,
    val match: Int
)

data class ListaDeseosState(
    val busqueda: String = "",
    val plantasDeseadas: List<PlantaDeseada> = listOf(
        PlantaDeseada(
            id = "zamioculcas",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Zamioculcas Zamiifolia",
            match = 95
        ),
        PlantaDeseada(
            id = "ficus-lyrata",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Ficus Lyrata",
            match = 88
        ),
        PlantaDeseada(
            id = "calathea-orbifolia",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Calathea Orbifolia",
            match = 91
        ),
        PlantaDeseada(
            id = "pilea-peperomioides",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Pilea Peperomioides",
            match = 97
        )
    )
) {
    val plantasFiltradas: List<PlantaDeseada>
        get() {
            val texto = busqueda.trim()

            return if (texto.isEmpty()) {
                plantasDeseadas
            } else {
                plantasDeseadas.filter { planta ->
                    planta.nombre.contains(
                        other = texto,
                        ignoreCase = true
                    )
                }
            }
        }
}