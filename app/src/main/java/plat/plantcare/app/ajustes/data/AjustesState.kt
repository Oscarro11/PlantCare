package plat.plantcare.app.ajustes.data

import plat.plantcare.app.collection.allPlants.data.CollectionLayout

data class AjustesState(
    val fotoUrl: String? = null,
    val plantasVivas: Int = 0,
    val riegosHechos: Int = 0,
    val diasRacha: Int = 0,

    val recordatoriosInteligentes: Boolean = false,
    val alertasClimaticas: Boolean = true,
    val horaAviso: String = "09:00",

    val ubicacion: String = "Sin ubicación registrada",

    val preferencesError: String? = null,
    val vistaColeccion: CollectionLayout = CollectionLayout.GRID
)