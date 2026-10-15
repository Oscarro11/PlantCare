package plat.plantcare.app.home.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.TopLevelRoute

@Serializable data object HomeGraph: TopLevelRoute

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    navigate(HomeGraph, navOptions)
}

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onDiagnosisClick: () -> Unit,
    onAddPlantClick: () -> Unit
) {
    navigation<HomeGraph>(startDestination = HomeDestination){
        homeScreen(
            onSeeAll = navController::navigateToAllTasksDestination,
            onDiagnosis = onDiagnosisClick,
            onAddPlant = onAddPlantClick
        )

        allTasksScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}