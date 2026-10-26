package plat.plantcare.app.favorites.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.TopLevelRoute
import plat.plantcare.app.favorites.data.PlantaDeseada

@Serializable
data object FavoritesGraph : TopLevelRoute

fun NavController.navigateToFavoritesGraph(
    navOptions: NavOptions? = null
) {
    navigate(FavoritesGraph, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    onAgregarClick: () -> Unit,
    onFiltrosClick: () -> Unit,
    onMoverAColeccion: (PlantaDeseada) -> Unit
) {
    navigation<FavoritesGraph>(
        startDestination = ListaDeseosDestination
    ) {
        listaDeseosScreen(
            onAgregarClick = onAgregarClick,
            onFiltrosClick = onFiltrosClick,
            onMoverAColeccion = onMoverAColeccion
        )
    }
}