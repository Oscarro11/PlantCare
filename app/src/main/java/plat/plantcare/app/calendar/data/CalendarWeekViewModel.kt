package plat.plantcare.app.calendar.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalendarWeekViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalendarWeekState())
    val state: StateFlow<CalendarWeekState> = _state.asStateFlow()

    fun onEvent(event: CalendarWeekEvent) {
        when (event) {
            is CalendarWeekEvent.SelectFilter ->
                _state.update { it.copy(selectedFilter = event.filter) }
            is CalendarWeekEvent.SelectDay ->
                _state.update { it.copy(selectedDay = event.day) }
        }
    }
}
