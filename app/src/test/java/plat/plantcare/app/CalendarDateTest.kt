package plat.plantcare.app.calendar

import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.calendar.data.*
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarDateTest {
    private fun task(id: String, date: String, action: TaskAction = TaskAction.WATER, completed: Boolean = false) =
        Task(id, "plant", LocalDate.parse(date), action, "Cuidado", completed, null)

    @Test fun leapFebruaryContainsAllDaysAndCompleteWeeks() {
        val dates = monthDates(YearMonth.of(2024, 2))
        assertEquals(DayOfWeek.MONDAY, dates.first().dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, dates.last().dayOfWeek)
        assertEquals(0, dates.size % 7)
        assertEquals(29, dates.count { it.monthValue == 2 })
        assertEquals(dates.size, dates.distinct().size)
    }
    @Test fun sixWeekMonthDoesNotDropLastDays() {
        val dates = monthDates(YearMonth.of(2026, 11))
        assertEquals(42, dates.size)
        assertTrue(LocalDate.of(2026, 11, 30) in dates)
    }
    @Test fun weekCrossesTheYearBoundary() {
        val dates = weekDates(LocalDate.of(2027, 1, 1))
        assertEquals(LocalDate.of(2026, 12, 28), dates.first())
        assertEquals(LocalDate.of(2027, 1, 3), dates.last())
    }
    @Test fun dailyFiltersUseFullDateAndRetainCompletedHistory() {
        val selected = LocalDate.of(2026, 11, 18)
        val tasks = listOf(task("water", "2026-11-18"), task("fertilize", "2026-11-18", TaskAction.FERTILIZE),
            task("past", "2026-10-18"), task("done", "2026-11-18", completed = true))
        val state = CalendarState(selectedDate = selected, tasks = tasks, filter = CalendarFilter.WATER)
        assertEquals(listOf("water", "done"), state.visibleTasks.map { it.id })
        assertEquals(listOf("fertilize"), state.copy(filter = CalendarFilter.NUTRITION).visibleTasks.map { it.id })
    }
    @Test fun periodCountsFollowTheVisibleWeek() {
        val state = CalendarState(selectedDate = LocalDate.of(2026, 11, 18), view = CalendarView.WEEK,
            tasks = listOf(task("here", "2026-11-18"), task("later", "2026-11-25")))
        assertEquals(listOf("here"), state.periodTasks.map { it.id })
        assertTrue(state.copy(selectedDate = state.selectedDate.plusWeeks(1)).periodTasks.any { it.id == "later" })
    }
}
