package plat.plantcare.app.discover.data

import androidx.annotation.DrawableRes
import plat.plantcare.app.R

data class PlantaRecomendada(
    val id: String,
    @DrawableRes val imagen: Int,
    val nombre: String,
    val nombreAlternativo: String,
    val caracteristica: String,
    val match: Int,
    val enListaDeseos: Boolean = false
)

data class DescubrirState(
    val plantasRecomendadas: List<PlantaRecomendada> = listOf(
        PlantaRecomendada(
            id = "zamioculcas",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Zamioculcas Zamiifolia",
            nombreAlternativo = "ZZ Plant",
            caracteristica = "RESISTENCIA EXTREMA",
            match = 98
        ),
        PlantaRecomendada(
            id = "maranta",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Maranta Leuconeura",
            nombreAlternativo = "Planta de la oración",
            caracteristica = "MOVIMIENTO FOLIAR",
            match = 94
        ),
        PlantaRecomendada(
            id = "monstera-adansonii",
            imagen = R.drawable.ic_launcher_background,
            nombre = "Monstera Adansonii",
            nombreAlternativo = "Monkey Mask",
            caracteristica = "TREPADORA / COLGANTE",
            match = 91
        )
    )
)