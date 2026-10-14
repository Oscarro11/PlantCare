package plat.plantcare.app.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import plat.plantcare.app.core.EmptyAppScaffold
import plat.plantcare.app.home.data.AllTasksViewModel
import plat.plantcare.app.home.data.HomeTaskCardUiState

@Composable
fun AllTasksRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AllTasksViewModel = viewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    AllTasksScreen(
        onBackClick = onBackClick,
        tasks = state.value.tasks,
        modifier = modifier
    )
}

@Composable
private fun AllTasksScreen(
    onBackClick: () -> Unit,
    tasks: List<HomeTaskCardUiState>,
    modifier: Modifier = Modifier
){
    EmptyAppScaffold(
        title = "Tareas pendientes",
        onBackClick = onBackClick,
        showBottomBar = true
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
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
                    size = null
                )
            }
        }
    }
}