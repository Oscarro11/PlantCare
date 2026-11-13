
package plat.plantcare.app.register.ui

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.register.data.RegisterPlantViewModel

@Serializable
data object RegisterPlantGraph

@Serializable
data object RegisterPlantStep1Destination

@Serializable
data object RegisterPlantStep2Destination

@Serializable
data object RegisterPlantStep3Destination

fun NavGraphBuilder.registerPlantGraph(
    navController: NavController
) {
    navigation<RegisterPlantGraph>(
        startDestination = RegisterPlantStep1Destination
    ) {

        // PASO 1: Identidad y fotografías
        composable<RegisterPlantStep1Destination> { entry ->

            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }

            val vm: RegisterPlantViewModel = hiltViewModel(
                viewModelStoreOwner = parent
            )

            RegisterPlantStep1Route(
                viewModel = vm,
                onContinue = {
                    navController.navigate(RegisterPlantStep2Destination)
                },
                onClose = {
                    navController.popBackStack()
                },
                onDiscard = {
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            )
        }

        // PASO 2: Requerimientos ambientales
        composable<RegisterPlantStep2Destination> { entry ->

            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }

            val vm: RegisterPlantViewModel = hiltViewModel(
                viewModelStoreOwner = parent
            )

            RegisterPlantStep2Route(
                viewModel = vm,
                onBack = {
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate(RegisterPlantStep3Destination)
                },
                onDiscard = {
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            )
        }

        // PASO 3: Rutina de cuidados
        composable<RegisterPlantStep3Destination> { entry ->

            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }

            val vm: RegisterPlantViewModel = hiltViewModel(
                viewModelStoreOwner = parent
            )

            RegisterPlantStep3Route(
                viewModel = vm,
                onBack = {
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                },
                onDiscard = {
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                },
                onSaved = {
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            )
        }
    }
}
