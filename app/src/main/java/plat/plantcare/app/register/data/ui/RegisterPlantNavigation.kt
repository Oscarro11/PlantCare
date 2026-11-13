package plat.plantcare.app.register.ui

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.register.data.RegisterPlantViewModel
import plat.plantcare.app.RegisterPlantStep1Route
import plat.plantcare.app.RegisterPlantStep2Route
import plat.plantcare.app.RegisterPlantStep3Route

@Serializable data object RegisterPlantGraph
@Serializable data object RegisterPlantStep1Destination
@Serializable data object RegisterPlantStep2Destination
@Serializable data object RegisterPlantStep3Destination

fun NavGraphBuilder.registerPlantGraph(navController: NavController) {
    navigation<RegisterPlantGraph>(startDestination = RegisterPlantStep1Destination) {
        composable<RegisterPlantStep1Destination> { entry ->
            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }
            val vm: RegisterPlantViewModel = hiltViewModel(viewModelStoreOwner = parent)
            RegisterPlantStep1Route(
                viewModel = vm,
                onContinue = { navController.navigate(RegisterPlantStep2Destination) },
                onClose = { navController.popBackStack() },
                onDiscard = { navController.popBackStack(RegisterPlantGraph, inclusive = true) }
            )
        }
        composable<RegisterPlantStep2Destination> { entry ->
            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }
            val vm: RegisterPlantViewModel = hiltViewModel(viewModelStoreOwner = parent)
            RegisterPlantStep2Route(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(RegisterPlantStep3Destination) },
                onDiscard = { navController.popBackStack(RegisterPlantGraph, inclusive = true) }
            )
        }
        composable<RegisterPlantStep3Destination> { entry ->
            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }
            val vm: RegisterPlantViewModel = hiltViewModel(viewModelStoreOwner = parent)
            RegisterPlantStep3Route(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack(RegisterPlantGraph, inclusive = true) },
                onDiscard = { navController.popBackStack(RegisterPlantGraph, inclusive = true) }
            )
        }
    }
}
