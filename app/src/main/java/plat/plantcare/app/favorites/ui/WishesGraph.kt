package plat.plantcare.app.favorites.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.TopLevelRoute

@Serializable
data object WishesGraph : TopLevelRoute

fun NavController.navigateToFavoritesGraph(
    navOptions: NavOptions? = null
) {
    navigate(WishesGraph, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    onAgregarClick: () -> Unit,
    onFiltrosClick: () -> Unit
) {
    navigation<WishesGraph>(
        startDestination = ListaDeseosDestination
    ) {
        listaDeseosScreen(
            onAgregarClick = onAgregarClick,
            onFiltrosClick = onFiltrosClick
        )
    }
}