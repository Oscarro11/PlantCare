package plat.plantcare.app.favorites.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ListaDeseosDestination

fun NavController.navigateToListaDeseosDestination(
    navOptions: NavOptions? = null
) {
    navigate(ListaDeseosDestination, navOptions)
}

fun NavGraphBuilder.listaDeseosScreen(
    onAgregarClick: () -> Unit,
    onFiltrosClick: () -> Unit
) {
    composable<ListaDeseosDestination> {
        ListaDeseosRoute(
            onAgregarClick = onAgregarClick,
            onFiltrosClick = onFiltrosClick
        )
    }
}