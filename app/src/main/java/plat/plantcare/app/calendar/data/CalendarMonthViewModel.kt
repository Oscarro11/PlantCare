package plat.plantcare.app.calendar.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalendarMonthViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalendarMonthState())
    val state: StateFlow<CalendarMonthState> = _state.asStateFlow()

    fun onEvent(event: CalendarMonthEvent) {
        when (event) {
            is CalendarMonthEvent.SelectFilter ->
                _state.update { it.copy(selectedFilter = event.filter) }
            is CalendarMonthEvent.SelectDay ->
                _state.update { it.copy(selectedDay = event.day) }
        }
    }
}
