
package plat.plantcare.app.register.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.register.data.RegisterPlantEvent
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

        // PASO 1: Identidad y ubicación
        composable<RegisterPlantStep1Destination> { entry ->

            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }

            val vm: RegisterPlantViewModel = hiltViewModel(
                viewModelStoreOwner = parent
            )

            val state by vm.state.collectAsStateWithLifecycle()

            RegisterPlantStep1Screen(
                state = state,
                onEvent = vm::onEvent,

                onContinueClick = {
                    navController.navigate(RegisterPlantStep2Destination)
                },

                onCloseClick = {
                    navController.popBackStack()
                },

                onDiscardClick = {
                    vm.onEvent(RegisterPlantEvent.Discard)
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

            val state by vm.state.collectAsStateWithLifecycle()

            RegisterPlantStep2Screen(
                state = state,
                onEvent = vm::onEvent,

                onBackClick = {
                    navController.popBackStack()
                },

                onCloseClick = {
                    navController.popBackStack()
                },

                onContinueClick = {
                    navController.navigate(RegisterPlantStep3Destination)
                },

                onDiscardClick = {
                    vm.onEvent(RegisterPlantEvent.Discard)
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            )
        }

        // PASO 3: Rutina de cuidados y guardado
        composable<RegisterPlantStep3Destination> { entry ->

            val parent = remember(entry) {
                navController.getBackStackEntry<RegisterPlantGraph>()
            }

            val vm: RegisterPlantViewModel = hiltViewModel(
                viewModelStoreOwner = parent
            )

            val state by vm.state.collectAsStateWithLifecycle()

            // Esperar a que termine el guardado
            LaunchedEffect(state.saved) {
                if (state.saved) {
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            }

            RegisterPlantStep3Screen(
                state = state,
                onEvent = vm::onEvent,

                onBackClick = {
                    navController.popBackStack()
                },

                onCloseClick = {
                    navController.popBackStack()
                },

                onSaveClick = {
                    vm.onEvent(RegisterPlantEvent.Save)
                },

                onDiscardClick = {
                    vm.onEvent(RegisterPlantEvent.Discard)
                    navController.popBackStack(
                        RegisterPlantGraph,
                        inclusive = true
                    )
                }
            )
        }
    }
}
