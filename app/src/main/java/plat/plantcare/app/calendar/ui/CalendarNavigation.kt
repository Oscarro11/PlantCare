package plat.plantcare.app.calendar.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.TopLevelRoute

@Serializable data object CalendarGraph : TopLevelRoute
@Serializable data object CalendarMonthDestination
@Serializable data object CalendarWeekDestination

fun NavGraphBuilder.calendarGraph(navController: NavController) {
    navigation<CalendarGraph>(startDestination = CalendarMonthDestination) {
        composable<CalendarMonthDestination> {
            CalendarMonthRoute(onWeekViewClick = {
                navController.navigate(CalendarWeekDestination)
            })
        }
        composable<CalendarWeekDestination> {
            CalendarWeekRoute(onMonthViewClick = {
                navController.popBackStack()
            })
        }
    }
}
