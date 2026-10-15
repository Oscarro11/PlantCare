package plat.plantcare.app.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.home.data.AllTasksEvent
import plat.plantcare.app.home.data.AllTasksViewModel
import plat.plantcare.app.home.data.HomeEvent
import plat.plantcare.app.home.data.HomeTaskCardUiState

@Serializable data object AllTasksDestination

fun NavController.navigateToAllTasksDestination(navOptions: NavOptions? = null) {
    navigate(AllTasksDestination, navOptions)
}

fun NavGraphBuilder.allTasksScreen(
    onBackClick: () -> Unit
) {
    composable<AllTasksDestination> {
        AllTasksRoute(
            onBackClick = onBackClick
        )
    }
}

@Composable
fun AllTasksRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AllTasksViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    AllTasksScreen(
        onBackClick = onBackClick,
        onTaskCompletion = { id -> viewModel.onEvent(AllTasksEvent.CompleteTask(id)) },
        tasks = state.value.tasks,
        modifier = modifier
    )
}

@Composable
private fun AllTasksScreen(
    onBackClick: () -> Unit,
    onTaskCompletion: (String) -> Unit,
    tasks: List<HomeTaskCardUiState>,
    modifier: Modifier = Modifier
){
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "Lista de tareas",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 18.dp,
                top = 18.dp,
                end = 18.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = tasks
            ) { task ->
                TaskCard(
                    state = task,
                    onCompletion = onTaskCompletion,
                    size = null
                )
            }
        }
    }


}