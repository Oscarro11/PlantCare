package plat.plantcare.app.calendar.data

import plat.plantcare.app.CalendarFilter

data class CalendarWeekState(
    val selectedFilter: CalendarFilter = CalendarFilter.ALL,
    val selectedDay: String = "18"
)
