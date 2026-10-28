package plat.plantcare.app.discover.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.DescubrirDestination
import plat.plantcare.app.core.navigation.TopLevelRoute
import plat.plantcare.app.descubrirScreen

@Serializable
data object DiscoverGraph : TopLevelRoute

fun NavController.navigateToDiscoverGraph(
    navOptions: NavOptions? = null
) {
    navigate(DiscoverGraph, navOptions)
}

fun NavGraphBuilder.discoverGraph(
    onVerCuidados: (String) -> Unit,
    onFiltrosClick: () -> Unit
) {
    navigation<DiscoverGraph>(
        startDestination = DescubrirDestination
    ) {
        descubrirScreen(
            onVerCuidados = onVerCuidados,
            onFiltrosClick = onFiltrosClick
        )
    }
}