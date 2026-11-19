package plat.plantcare.app.calendar.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.repositories.TaskRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject

enum class CalendarFilter(val label: String) { ALL("Todo"), WATER("Riego"), NUTRITION("Nutrición") }
enum class CalendarView { MONTH, WEEK }

fun CalendarFilter.matches(task: Task): Boolean = when (this) {
    CalendarFilter.ALL -> true
    CalendarFilter.WATER -> task.action == TaskAction.WATER
    CalendarFilter.NUTRITION -> task.action == TaskAction.FERTILIZE
}

fun monthDates(month: YearMonth): List<LocalDate> {
    val first = month.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val last = month.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    return List((java.time.temporal.ChronoUnit.DAYS.between(first, last) + 1).toInt()) { first.plusDays(it.toLong()) }
}
fun weekDates(date: LocalDate): List<LocalDate> {
    val first = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return List(7) { first.plusDays(it.toLong()) }
}

data class CalendarState(
    val selectedDate: LocalDate = LocalDate.now(),
    val view: CalendarView = CalendarView.MONTH,
    val filter: CalendarFilter = CalendarFilter.ALL,
    val tasks: List<Task> = emptyList(),
    val plants: List<Plant> = emptyList(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val completing: Set<String> = emptySet(),
    val created: Int = 0,
    val error: String? = null
) {
    val dates get() = if (view == CalendarView.MONTH) monthDates(YearMonth.from(selectedDate)) else weekDates(selectedDate)
    val periodTasks get() = tasks.filter { it.dueDate in dates.first()..dates.last() }
    val visibleTasks get() = tasks.filter { it.dueDate == selectedDate && filter.matches(it) }
        .sortedWith(compareBy<Task> { it.completed }.thenBy { it.action.ordinal }.thenBy { it.plantName })
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TaskRepository,
    plants: PlantRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val local = MutableStateFlow(CalendarState(
        selectedDate = savedState.get<String>("date")?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now(),
        view = savedState.get<String>("view")?.let { runCatching { CalendarView.valueOf(it) }.getOrNull() } ?: CalendarView.MONTH,
        filter = savedState.get<String>("filter")?.let { runCatching { CalendarFilter.valueOf(it) }.getOrNull() } ?: CalendarFilter.ALL
    ))
    private val retry = MutableStateFlow(0)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state = retry.flatMapLatest {
        combine(repository.observeAllTasks(), plants.observePlants(), local) { tasks, collection, ui ->
            val byId = collection.associateBy { it.id }
            ui.copy(tasks = tasks.map { task ->
                byId[task.plantId]?.let { plant -> task.copy(plantName = plant.name, plantLocation = plant.location) } ?: task
            }, plants = collection, loading = false)
        }.catch { emit(local.value.copy(loading = false, error = "No se pudo cargar el calendario. Intenta de nuevo.")) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), local.value)

    fun select(date: LocalDate) { savedState["date"] = date.toString(); local.update { it.copy(selectedDate = date) } }
    fun changeView(view: CalendarView) { savedState["view"] = view.name; local.update { it.copy(view = view) } }
    fun filter(filter: CalendarFilter) { savedState["filter"] = filter.name; local.update { it.copy(filter = filter) } }
    fun move(direction: Long) = select(if (local.value.view == CalendarView.MONTH) local.value.selectedDate.plusMonths(direction) else local.value.selectedDate.plusWeeks(direction))
    fun retry() { local.update { it.copy(error = null, loading = true) }; retry.update { it + 1 } }
    fun clearError() { local.update { it.copy(error = null) } }

    fun complete(task: Task, completedOn: LocalDate) {
        if (task.completed || task.id in local.value.completing) return
        local.update { it.copy(completing = it.completing + task.id, error = null) }
        viewModelScope.launch {
            try { repository.completeTask(task.id, completedOn) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(error = "No se pudo completar la tarea. Intenta de nuevo.") } }
            finally { local.update { it.copy(completing = it.completing - task.id) } }
        }
    }

    fun create(plantId: String, action: TaskAction, due: LocalDate, interval: String, description: String) {
        if (local.value.saving) return
        val plant = state.value.plants.firstOrNull { it.id == plantId } ?: return
        val days = if (action == TaskAction.WATER && interval.isNotBlank()) interval.toIntOrNull() else null
        if (action == TaskAction.WATER && interval.isNotBlank() && (days == null || days !in 1..365)) {
            local.update { it.copy(error = "El intervalo debe ser de 1 a 365 días.") }; return
        }
        local.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                repository.createTask(Task(UUID.randomUUID().toString(), plantId, due, action,
                    description.trim().ifBlank { action.displayName }, false, null, days,
                    plantName = plant.name, plantImageUrl = plant.imageUrl.orEmpty(), plantLocation = plant.location))
                select(due)
                local.update { it.copy(saving = false, created = it.created + 1) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(saving = false, error = "No se pudo guardar la tarea. Intenta de nuevo.") } }
        }
    }
}
