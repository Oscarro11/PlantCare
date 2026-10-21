package plat.plantcare.app.collection.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.TopLevelRoute

@Serializable data object CollectionGraph: TopLevelRoute

fun NavController.navigateToCollectionGraph(navOptions: NavOptions? = null) {
    navigate(CollectionGraph, navOptions)
}

fun NavGraphBuilder.collectionGraph(
    navController: NavController,
    onAddPlantClick: () -> Unit,
) {
    navigation<CollectionGraph>(startDestination = CollectionDestination) {
        collectionScreen(
            onAddPlant = onAddPlantClick,
            //TODO: add navigation to specific plant
            onExaminePlant = {}
        )
    }
}