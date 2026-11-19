package plat.plantcare.app.tasks

import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.tasks.nextWatering
import plat.plantcare.app.notifications.ReminderSettings
import plat.plantcare.app.notifications.reminderDelay
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class TaskRecurrenceTest {
    private fun watering() = Task("watering", "plant", LocalDate.of(2026, 11, 1), TaskAction.WATER, "Riego", false, null, 7)
    @Test fun lateWateringStartsFromTheActualCompletionDay() {
        val next = watering().nextWatering(LocalDate.of(2026, 11, 12))!!
        assertEquals(LocalDate.of(2026, 11, 19), next.dueDate)
        assertFalse(next.completed)
        assertNull(next.completedAt)
        assertEquals("watering", next.parentTaskId)
    }
    @Test fun datesCrossMonthAndLeapYearBoundaries() {
        assertEquals(LocalDate.of(2028, 3, 6), watering().nextWatering(LocalDate.of(2028, 2, 28))!!.dueDate)
    }
    @Test fun aSingleWateringOrAnotherCareDoesNotGenerateARecurrence() {
        assertNull(watering().copy(recurrenceDays = null).nextWatering(LocalDate.now()))
        assertNull(watering().copy(action = TaskAction.PRUNE).nextWatering(LocalDate.now()))
    }
    @Test fun bothDevicesGenerateTheSameSuccessorId() {
        assertEquals(watering().nextWatering(LocalDate.of(2026, 11, 10))!!.id,
            watering().nextWatering(LocalDate.of(2026, 11, 11))!!.id)
    }
    @Test fun theNextReminderUsesLocalTimeAcrossDaylightSaving() {
        val now = ZonedDateTime.of(2026, 3, 7, 10, 0, 0, 0, ZoneId.of("America/New_York"))
        assertEquals(22, reminderDelay(ReminderSettings(true, 9, 0), now).toHours())
        assertEquals(23, reminderDelay(ReminderSettings(true, 9, 0), now.withHour(9)).toHours())
    }
}
