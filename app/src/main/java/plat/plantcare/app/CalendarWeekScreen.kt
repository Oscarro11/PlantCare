package plat.plantcare.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.core.FullAppScaffold
import plat.plantcare.app.ui.theme.PlantcareTheme


data class WeekTask(
    val plantName: String,
    val taskName: String,
    val time: String,
    val detail: String,
    @DrawableRes val icon: Int
)


private val weekDays = listOf(
    "L" to "14",
    "M" to "15",
    "X" to "16",
    "J" to "17",
    "V" to "18",
    "S" to "19",
    "D" to "20"
)


private val morningTasks = listOf(
    WeekTask(
        plantName = "Monty",
        taskName = "Riego",
        time = "09:30",
        detail = "350 ml agua filtrada",
        icon = R.drawable.ic_water
    ),
    WeekTask(
        plantName = "Rayas",
        taskName = "Pulverizar",
        time = "11:00",
        detail = "Bruma fina · 65% hum.",
        icon = R.drawable.ic_humidity
    )
)


private val afternoonTasks = listOf(
    WeekTask(
        plantName = "Apolo",
        taskName = "Abono",
        time = "17:30",
        detail = "Dosis suave bioestimulante",
        icon = R.drawable.ic_nutrition
    )
)


@Composable
private fun WeekFilters(
    selectedFilter: CalendarFilter,
    onFilterChange: (CalendarFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CalendarFilterChip(
            text = "Todo 18",
            selected = selectedFilter == CalendarFilter.ALL,
            onClick = {
                onFilterChange(CalendarFilter.ALL)
            }
        )

        CalendarFilterChip(
            text = "Riego 10",
            icon = R.drawable.ic_water,
            selected = selectedFilter == CalendarFilter.WATER,
            onClick = {
                onFilterChange(CalendarFilter.WATER)
            }
        )

        CalendarFilterChip(
            text = "Nutrición 4",
            icon = R.drawable.ic_nutrition,
            selected = selectedFilter == CalendarFilter.NUTRITION,
            onClick = {
                onFilterChange(CalendarFilter.NUTRITION)
            }
        )
    }
}


@Composable
private fun WeekDayItem(
    letter: String,
    number: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = letter,
            fontSize = 10.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (selected) {
                        darkGreen
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) {
                    Color.White
                } else {
                    darkGreen
                }
            )
        }
    }
}


@Composable
private fun WeekDays(
    selectedDay: String,
    onDaySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        weekDays.forEach { (letter, number) ->
            WeekDayItem(
                letter = letter,
                number = number,
                selected = number == selectedDay,
                onClick = {
                    onDaySelected(number)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun WeekTaskCard(
    task: WeekTask,
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)
    val lightGreen = Color(0xFFDDF2E5)

    var completed by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = lightGreen,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = task.icon),
                contentDescription = task.taskName,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF52755C)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = task.plantName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Text(
                    text = task.taskName,
                    fontSize = 10.sp,
                    color = Color(0xFF52755C)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Hora",
                    modifier = Modifier.size(12.dp),
                    tint = Color.Gray
                )

                Text(
                    text = "${task.time} · ${task.detail}",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (completed) {
                        darkGreen
                    } else {
                        lightGreen
                    },
                    shape = CircleShape
                )
                .clickable {
                    completed = !completed
                },
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Text(
                    text = "✓",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
private fun WeekTaskSection(
    title: String,
    tasks: List<WeekTask>,
    onTaskClick: (WeekTask) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        tasks.forEach { task ->
            WeekTaskCard(
                task = task,
                onClick = {
                    onTaskClick(task)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun WeekTasks(
    onTaskClick: (WeekTask) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Hoy",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        WeekTaskSection(
            title = "MAÑANA · 09:00 - 12:00",
            tasks = morningTasks,
            onTaskClick = onTaskClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeekTaskSection(
            title = "TARDE · 16:00 - 19:00",
            tasks = afternoonTasks,
            onTaskClick = onTaskClick
        )
    }
}


@Composable
private fun WeekHeader(
    onMonthViewClick: () -> Unit,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "PLANIFICADOR BOTÁNICO",
            fontSize = 10.sp,
            color = darkGreen
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "‹",
                fontSize = 30.sp,
                color = darkGreen,
                modifier = Modifier
                    .clickable(onClick = onPreviousWeekClick)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Otoño · Semana 42",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Text(
                    text = "14 – 20 Oct 2024",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkGreen
                )
            }

            Text(
                text = "›",
                fontSize = 30.sp,
                color = darkGreen,
                modifier = Modifier
                    .clickable(onClick = onNextWeekClick)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CalendarViewSelector(
                selectedView = CalendarView.WEEK,
                onViewChange = { view ->
                    if (view == CalendarView.MONTH) {
                        onMonthViewClick()
                    }
                }
            )
        }
    }
}


@Composable
fun CalendarWeekScreen(
    onMonthViewClick: () -> Unit = {},
    onPreviousWeekClick: () -> Unit = {},
    onNextWeekClick: () -> Unit = {},
    onTaskClick: (WeekTask) -> Unit = {}
) {
    val backgroundColor = Color(0xFFEAF9F0)

    var selectedFilter by remember {
        mutableStateOf(CalendarFilter.ALL)
    }

    var selectedDay by remember {
        mutableStateOf("18")
    }

    FullAppScaffold(
        section = "Calendario"
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            WeekHeader(
                onMonthViewClick = onMonthViewClick,
                onPreviousWeekClick = onPreviousWeekClick,
                onNextWeekClick = onNextWeekClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            WeekFilters(
                selectedFilter = selectedFilter,
                onFilterChange = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            WeekDays(
                selectedDay = selectedDay,
                onDaySelected = {
                    selectedDay = it
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            WeekTasks(
                onTaskClick = onTaskClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CalendarWeekScreenPreview() {
    PlantcareTheme {
        CalendarWeekScreen()
    }
}