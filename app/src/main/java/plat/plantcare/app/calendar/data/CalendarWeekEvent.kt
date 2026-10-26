package plat.plantcare.app.calendar.data

import plat.plantcare.app.CalendarFilter

sealed interface CalendarWeekEvent {
    data class SelectFilter(val filter: CalendarFilter) : CalendarWeekEvent
    data class SelectDay(val day: String) : CalendarWeekEvent
}
