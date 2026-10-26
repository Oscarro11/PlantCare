package plat.plantcare.app.collection

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.collection.allPlants.ui.CollectionDestination
import plat.plantcare.app.collection.allPlants.ui.collectionScreen
import plat.plantcare.app.collection.plantDetails.ui.navigateToPlantDetailsDestination
import plat.plantcare.app.collection.plantDetails.ui.plantDetailsScreen
import plat.plantcare.app.core.navigation.TopLevelRoute

@Serializable data object CollectionGraph: TopLevelRoute

fun NavController.navigateToCollectionGraph(navOptions: NavOptions? = null) {
    navigate(CollectionGraph, navOptions)
}

fun NavGraphBuilder.collectionGraph(
    navController: NavController,
    onAddPlantClick: () -> Unit,
    onMakeDiagnostic: () -> Unit
) {
    navigation<CollectionGraph>(startDestination = CollectionDestination) {
        collectionScreen(
            onAddPlant = onAddPlantClick,
            onExaminePlant = navController::navigateToPlantDetailsDestination
        )

        plantDetailsScreen(
            onBackClick = { navController.popBackStack() },
            onMakeDiagnostic = onMakeDiagnostic
        )
    }
}