package plat.plantcare.app.calendar.data

import plat.plantcare.app.CalendarFilter

sealed interface CalendarMonthEvent {
    data class SelectFilter(val filter: CalendarFilter) : CalendarMonthEvent
    data class SelectDay(val day: Int) : CalendarMonthEvent
}
