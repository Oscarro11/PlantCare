package plat.plantcare.app.calendar

import android.graphics.Bitmap
import java.io.File
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.calendar.data.*
import plat.plantcare.app.calendar.ui.CalendarScreen
import plat.plantcare.app.ui.theme.PlantcareTheme
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CalendarUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun darkCalendarNavigatesAcrossYearAndChangesView() = verify(true)
    @Test fun lightCalendarNavigatesAcrossYearAndChangesView() = verify(false)

    private fun verify(dark: Boolean) {
        compose.setContent {
            var state by remember { mutableStateOf(CalendarState(selectedDate = LocalDate.of(2026, 12, 18), loading = false)) }
            PlantcareTheme(darkTheme = dark) {
                CalendarScreen(state, { state = state.copy(selectedDate = it) }, { state = state.copy(view = it) },
                    { state = state.copy(filter = it) }, { state = state.copy(selectedDate = if (state.view == CalendarView.MONTH) state.selectedDate.plusMonths(it) else state.selectedDate.plusWeeks(it)) },
                    {}, {}, {}, {})
            }
        }
        compose.onNodeWithContentDescription("Periodo siguiente").performClick()
        compose.onNodeWithText("enero 2027").assertIsDisplayed()
        compose.onNodeWithText("Semana").performClick()
        compose.onNodeWithText("Mes").performClick()
        compose.onNodeWithContentDescription("lunes 18 de enero de 2027, 0 tareas").assertIsSelected()
        compose.onNodeWithText("Nueva tarea").performScrollTo().assertIsDisplayed()
        compose.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            File(context.getExternalFilesDir(null), if (dark) "calendar-dark.png" else "calendar-light.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
