package plat.plantcare.app.calendar.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import plat.plantcare.app.R
import plat.plantcare.app.calendar.data.*
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.home.allTasks.data.AllTasksState
import plat.plantcare.app.home.allTasks.ui.NewTaskDialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val spanish = Locale.forLanguageTag("es")
private val fullDate = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", spanish)

@Composable
fun CalendarRoute(viewModel: CalendarViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var creating by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(state.created) { if (state.created > 0) creating = false }
    CalendarScreen(state, viewModel::select, viewModel::changeView, viewModel::filter,
        viewModel::move, { viewModel.select(LocalDate.now()) },
        { viewModel.clearError(); creating = true }, viewModel::retry, onComplete = { task ->
            val today = LocalDate.now()
            DatePickerDialog(context, { _, y, m, d -> viewModel.complete(task, LocalDate.of(y, m + 1, d)) },
                today.year, today.monthValue - 1, today.dayOfMonth).apply {
                setTitle("¿Cuándo realizaste este cuidado?")
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        })
    if (creating) NewTaskDialog(
        state = AllTasksState(plants = state.plants, saving = state.saving, error = state.error),
        onDismiss = { if (!state.saving) creating = false }, onSave = viewModel::create,
        initialDue = state.selectedDate
    )
}

@Composable
fun CalendarScreen(
    state: CalendarState,
    onSelect: (LocalDate) -> Unit,
    onView: (CalendarView) -> Unit,
    onFilter: (CalendarFilter) -> Unit,
    onMove: (Long) -> Unit,
    onToday: () -> Unit,
    onAdd: () -> Unit,
    onRetry: () -> Unit,
    onComplete: (Task) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("PLANIFICADOR BOTÁNICO", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMove(-1) }) { Icon(painterResource(R.drawable.arrow_back), "Periodo anterior", tint = MaterialTheme.colorScheme.onBackground) }
                val title = if (state.view == CalendarView.MONTH)
                    state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", spanish))
                else "${state.dates.first().format(DateTimeFormatter.ofPattern("d MMM", spanish))} – ${state.dates.last().format(DateTimeFormatter.ofPattern("d MMM yyyy", spanish))}"
                Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground)
                IconButton(onClick = { onMove(1) }) { Icon(painterResource(R.drawable.arrow_forward), "Periodo siguiente", tint = MaterialTheme.colorScheme.onBackground) }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onToday) { Text("Hoy") }
                Spacer(Modifier.weight(1f))
                CalendarView.entries.forEach { view ->
                    FilterChip(selected = state.view == view, onClick = { onView(view) },
                        label = { Text(if (view == CalendarView.MONTH) "Mes" else "Semana") }, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalendarFilter.entries.forEach { filter ->
                    FilterChip(selected = state.filter == filter, onClick = { onFilter(filter) },
                        label = { Text("${filter.label} (${state.periodTasks.count { filter.matches(it) }})") })
                }
            }
        }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        state.error?.let { message -> item {
            Column {
                Text(message, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onRetry) { Text("Reintentar") }
            }
        } }
        item {
            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.padding(8.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                            Box(Modifier.weight(1f).height(32.dp), contentAlignment = Alignment.Center) {
                                Text(day, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    state.dates.chunked(7).forEach { week ->
                        Row(Modifier.fillMaxWidth()) {
                            week.forEach { date ->
                                val tasks = state.tasks.filter { it.dueDate == date && state.filter.matches(it) }
                                CalendarDate(date, date == state.selectedDate, date == LocalDate.now(),
                                    state.view == CalendarView.WEEK || YearMonth.from(date) == YearMonth.from(state.selectedDate),
                                    tasks.size, tasks.any { !it.completed }, { onSelect(date) }, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        item {
            Text(state.selectedDate.format(fullDate), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("${state.visibleTasks.count { !it.completed }} pendientes", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                FilledTonalButton(onClick = onAdd, enabled = !state.loading) { Text("Nueva tarea") }
            }
        }
        if (!state.loading && state.visibleTasks.isEmpty()) item {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Text(if (state.filter == CalendarFilter.ALL) "No hay cuidados programados para este día." else "No hay tareas de ${state.filter.label.lowercase(spanish)} para este día.",
                    Modifier.fillMaxWidth().padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(state.visibleTasks, key = { it.id }) { task -> CalendarTaskCard(task, task.id in state.completing) { onComplete(task) } }
        val pendingSync = state.tasks.count { !it.isSynced }
        if (pendingSync > 0) item { Text("$pendingSync cambios pendientes de sincronizar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun CalendarDate(date: LocalDate, selected: Boolean, today: Boolean, currentMonth: Boolean,
    count: Int, pending: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val content = if (selected) colors.onPrimaryContainer else if (currentMonth) colors.onSurface else colors.onSurfaceVariant
    Surface(onClick = onClick, modifier = modifier.padding(2.dp).heightIn(min = 64.dp).semantics {
        contentDescription = "${date.format(fullDate)}, $count tareas${if (today) ", hoy" else ""}"
        this.selected = selected
    }, shape = MaterialTheme.shapes.small,
        color = if (selected) colors.primaryContainer else colors.surfaceContainer,
        contentColor = content) {
        Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = if (today || selected) FontWeight.Bold else FontWeight.Normal)
            if (count > 0) Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = if (selected) content else if (pending) colors.primary else colors.onSurfaceVariant)
            else if (today) Box(Modifier.padding(top = 5.dp).size(5.dp).background(content, CircleShape))
        }
    }
}

@Composable
private fun CalendarTaskCard(task: Task, completing: Boolean, onComplete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(painterResource(task.action.iconId), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(task.plantName.ifBlank { "Planta" }, style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(task.action.displayName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text(task.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (task.completed) Text("Realizada: ${task.completedAt ?: task.dueDate}", style = MaterialTheme.typography.labelSmall)
                else if (task.dueDate.isBefore(LocalDate.now())) Text("Atrasada", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }
            Checkbox(checked = task.completed, onCheckedChange = { if (it) onComplete() }, enabled = !task.completed && !completing,
                modifier = Modifier.semantics { contentDescription = "Completar ${task.action.displayName} de ${task.plantName}" })
        }
    }
}
