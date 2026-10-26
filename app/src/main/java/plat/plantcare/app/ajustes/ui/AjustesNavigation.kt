package plat.plantcare.app.settings.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.AjustesRoute

@Serializable
data object AjustesDestination

fun NavController.navigateToAjustesDestination(
    navOptions: NavOptions? = null
) {
    navigate(AjustesDestination, navOptions)
}

fun NavGraphBuilder.ajustesScreen(
    onBackClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    composable<AjustesDestination> {
        AjustesRoute(
            onBackClick = onBackClick,
            onCerrarSesion = onCerrarSesion
        )
    }
}