package plat.plantcare.app.calendar.data

import plat.plantcare.app.calendar.ui.CalendarFilter

data class CalendarMonthState(
    val selectedFilter: CalendarFilter = CalendarFilter.ALL,
    val selectedDay: Int = 18
)
