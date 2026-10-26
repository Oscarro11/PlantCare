package plat.plantcare.app.ajustes.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object AjustesGraph

fun NavController.navigateToAjustesGraph(
    navOptions: NavOptions? = null
) {
    navigate(AjustesGraph, navOptions)
}

fun NavGraphBuilder.ajustesGraph(
    navController: NavController,
    onCerrarSesion: () -> Unit
) {
    navigation<AjustesGraph>(
        startDestination = AjustesDestination
    ) {
        ajustesScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onCerrarSesion = onCerrarSesion
        )
    }
}