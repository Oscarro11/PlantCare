package plat.plantcare.app.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import plat.plantcare.app.CalendarMonthScreen
import plat.plantcare.app.CalendarWeekScreen
import plat.plantcare.app.calendar.data.CalendarMonthViewModel
import plat.plantcare.app.calendar.data.CalendarWeekViewModel

@Composable
fun CalendarMonthRoute(
    onWeekViewClick: () -> Unit,
    viewModel: CalendarMonthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    CalendarMonthScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onWeekViewClick = onWeekViewClick
    )
}

@Composable
fun CalendarWeekRoute(
    onMonthViewClick: () -> Unit,
    viewModel: CalendarWeekViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    CalendarWeekScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onMonthViewClick = onMonthViewClick
    )
}
