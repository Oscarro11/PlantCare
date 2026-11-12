package plat.plantcare.app.home.allTasks.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.home.allTasks.data.AllTasksState
import plat.plantcare.app.home.allTasks.data.AllTasksViewModel
import plat.plantcare.app.home.home.ui.TaskCard
import java.time.LocalDate

@Serializable data object AllTasksDestination
fun NavController.navigateToAllTasksDestination(navOptions: NavOptions? = null) { navigate(AllTasksDestination, navOptions) }
fun NavGraphBuilder.allTasksScreen(onBackClick: () -> Unit) {
    composable<AllTasksDestination> { AllTasksRoute(onBackClick) }
}
@Composable
fun AllTasksRoute(onBackClick: () -> Unit, modifier: Modifier = Modifier, viewModel: AllTasksViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var creating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(state.created) { if (state.created > 0) creating = false }
    Scaffold(modifier = modifier, topBar = { AppTopBar(title = "Lista de tareas", onBackClick = onBackClick,
        actions = { TextButton(onClick = { creating = true }) { Text("Nueva tarea") } }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(selected = !state.showCompleted, onClick = { viewModel.history(false) }, label = { Text("Pendientes") })
                FilterChip(selected = state.showCompleted, onClick = { viewModel.history(true) }, label = { Text("Completadas") })
            } }
            if (state.isLoading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            state.error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
            if (state.pendingSync > 0) item { Text("Cambios pendientes de sincronizar: " + state.pendingSync, style = MaterialTheme.typography.bodySmall) }
            if (!state.isLoading && state.visibleTasks.isEmpty()) item { Text(if (state.showCompleted) "Aún no has completado tareas." else "No tienes tareas pendientes.") }
            items(state.visibleTasks, key = { it.id }) { task ->
                TaskCard(state = task, size = null, onCompletion = { id ->
                    if (!task.completed) {
                        val today = LocalDate.now()
                        DatePickerDialog(context, { _, year, month, day -> viewModel.complete(id, LocalDate.of(year, month + 1, day)) },
                            today.year, today.monthValue - 1, today.dayOfMonth).apply {
                            setTitle("¿Cuándo realizaste este cuidado?")
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    }
                })
            }
        }
    }
    if (creating) NewTaskDialog(state, onDismiss = { if (!state.saving) creating = false }, onSave = viewModel::create)
}

@Composable
private fun NewTaskDialog(state: AllTasksState, onDismiss: () -> Unit,
    onSave: (String, TaskAction, LocalDate, String, String) -> Unit) {
    var plantId by remember { mutableStateOf(state.plants.firstOrNull()?.id.orEmpty()) }
    var action by remember { mutableStateOf(TaskAction.WATER) }
    var due by remember { mutableStateOf(LocalDate.now()) }
    var interval by remember { mutableStateOf("7") }
    var description by remember { mutableStateOf("") }
    var plantMenu by remember { mutableStateOf(false) }
    var actionMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Nueva tarea") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (state.plants.isEmpty()) Text("Agrega una planta a tu colección antes de crear una tarea.")
            Box {
                TextButton(onClick = { plantMenu = true }, enabled = !state.saving) { Text(state.plants.firstOrNull { it.id == plantId }?.name ?: "Selecciona una planta") }
                DropdownMenu(expanded = plantMenu, onDismissRequest = { plantMenu = false }) {
                    state.plants.forEach { plant -> DropdownMenuItem(text = { Text(plant.name) }, onClick = { plantId = plant.id; plantMenu = false }) }
                }
            }
            Box {
                TextButton(onClick = { actionMenu = true }, enabled = !state.saving) { Text(action.displayName) }
                DropdownMenu(expanded = actionMenu, onDismissRequest = { actionMenu = false }) {
                    TaskAction.entries.forEach { option -> DropdownMenuItem(text = { Text(option.displayName) }, onClick = { action = option; actionMenu = false }) }
                }
            }
            TextButton(enabled = !state.saving, onClick = {
                DatePickerDialog(context, { _, y, m, d -> due = LocalDate.of(y, m + 1, d) }, due.year, due.monthValue - 1, due.dayOfMonth).show()
            }) { Text("Fecha prevista: " + due) }
            if (action == TaskAction.WATER) {
                OutlinedTextField(value = interval, onValueChange = { interval = it }, label = { Text("Repetir cada N días") },
                    supportingText = { Text("Déjalo vacío para un riego único. El próximo se calcula desde la fecha de realización.") }, enabled = !state.saving, singleLine = true)
            }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción (opcional)") }, enabled = !state.saving)
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }, confirmButton = { TextButton(enabled = plantId.isNotBlank() && !state.saving, onClick = { onSave(plantId, action, due, interval, description) }) { Text(if (state.saving) "Guardando…" else "Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !state.saving) { Text("Cancelar") } })
}
