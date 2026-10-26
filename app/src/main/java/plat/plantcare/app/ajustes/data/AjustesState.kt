package plat.plantcare.app.settings.data

import plat.plantcare.app.collection.data.CollectionLayout

data class AjustesState(
    val fotoUrl: String? = null,
    val plantasVivas: Int = 12,
    val riegosHechos: Int = 142,
    val diasRacha: Int = 98,

    val recordatoriosInteligentes: Boolean = true,
    val alertasClimaticas: Boolean = true,
    val horaAviso: String = "09:00 AM",

    val ubicacion: String = "Madrid, España (Templado-seco)",

    val vistaColeccion: CollectionLayout = CollectionLayout.GRID
)