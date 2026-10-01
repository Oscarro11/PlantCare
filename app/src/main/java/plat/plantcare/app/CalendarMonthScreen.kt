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


data class CalendarDay(
    val number: Int,
    val isCurrentMonth: Boolean = true,
    val eventColors: List<Color> = emptyList()
)

data class CareTask(
    val plantName: String,
    val description: String,
    @DrawableRes val icon: Int
)


private val wateringColor = Color(0xFF52755C)
private val nutritionColor = Color(0xFF52755C)
private val sprayColor = Color(0xFFF08068)
private val pruningColor = Color(0xFF7A210F)


private val octoberDays = listOf(
    CalendarDay(30, isCurrentMonth = false),

    CalendarDay(1, eventColors = listOf(wateringColor)),
    CalendarDay(2),
    CalendarDay(3, eventColors = listOf(wateringColor)),
    CalendarDay(4, eventColors = listOf(wateringColor, sprayColor)),
    CalendarDay(5),
    CalendarDay(6),

    CalendarDay(7, eventColors = listOf(wateringColor)),
    CalendarDay(8),
    CalendarDay(9, eventColors = listOf(wateringColor)),
    CalendarDay(10),
    CalendarDay(11, eventColors = listOf(wateringColor)),
    CalendarDay(12),
    CalendarDay(13),

    CalendarDay(14, eventColors = listOf(wateringColor, sprayColor)),
    CalendarDay(15, eventColors = listOf(wateringColor)),
    CalendarDay(16),
    CalendarDay(17),

    CalendarDay(
        18,
        eventColors = listOf(
            wateringColor,
            nutritionColor,
            sprayColor,
            pruningColor,
            wateringColor,
            nutritionColor,
            sprayColor
        )
    ),

    CalendarDay(19),
    CalendarDay(20),

    CalendarDay(21, eventColors = listOf(wateringColor)),
    CalendarDay(22),
    CalendarDay(23, eventColors = listOf(wateringColor)),
    CalendarDay(24, eventColors = listOf(wateringColor, sprayColor)),
    CalendarDay(25),
    CalendarDay(26),
    CalendarDay(27),

    CalendarDay(28, eventColors = listOf(wateringColor)),
    CalendarDay(29),
    CalendarDay(30),
    CalendarDay(31, eventColors = listOf(wateringColor)),

    CalendarDay(1, isCurrentMonth = false),
    CalendarDay(2, isCurrentMonth = false),
    CalendarDay(3, isCurrentMonth = false)
)


private val careTasks = listOf(
    CareTask(
        plantName = "Monty",
        description = "350 ml · Agua filtrada",
        icon = R.drawable.ic_water
    ),
    CareTask(
        plantName = "Rayas",
        description = "Humedad ambiental 65%",
        icon = R.drawable.ic_humidity
    ),
    CareTask(
        plantName = "Apolo",
        description = "Dosis suave bioestimulante",
        icon = R.drawable.ic_nutrition
    )
)


@Composable
private fun CalendarFilters(
    selectedFilter: CalendarFilter,
    onFilterChange: (CalendarFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarFilterChip(
            text = "Todo (18)",
            selected = selectedFilter == CalendarFilter.ALL,
            onClick = {
                onFilterChange(CalendarFilter.ALL)
            }
        )

        CalendarFilterChip(
            text = "Riego (10)",
            icon = R.drawable.ic_water,
            selected = selectedFilter == CalendarFilter.WATER,
            onClick = {
                onFilterChange(CalendarFilter.WATER)
            }
        )

        CalendarFilterChip(
            text = "Nutrición (4)",
            icon = R.drawable.ic_nutrition,
            selected = selectedFilter == CalendarFilter.NUTRITION,
            onClick = {
                onFilterChange(CalendarFilter.NUTRITION)
            }
        )
    }
}


@Composable
private fun CalendarEventIndicators(
    events: List<Color>,
    selected: Boolean
) {
    if (events.isEmpty()) return

    val visibleEvents = events.take(2)
    val remainingEvents = events.size - visibleEvents.size

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        visibleEvents.forEach { color ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (selected) {
                            color.copy(alpha = 0.9f)
                        } else {
                            color
                        },
                        shape = CircleShape
                    )
            )
        }

        if (remainingEvents > 0) {
            Text(
                text = "+$remainingEvents",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF52755C)
                }
            )
        }
    }
}


@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = modifier
            .padding(2.dp)
            .height(58.dp)
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color(0xFFE5F5EB)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = day.isCurrentMonth,
                onClick = onClick
            )
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.number.toString(),
            fontSize = 11.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = when {
                !day.isCurrentMonth -> Color.LightGray
                selected -> Color.White
                else -> darkGreen
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        CalendarEventIndicators(
            events = day.eventColors,
            selected = selected
        )
    }
}


@Composable
private fun LegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Text(
            text = text,
            fontSize = 8.sp,
            color = Color.DarkGray
        )
    }
}


@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(wateringColor, "Riego")
        LegendItem(nutritionColor, "Abono")
        LegendItem(sprayColor, "Pulverizado")
        LegendItem(pruningColor, "Poda")
    }
}


@Composable
private fun MonthCalendar(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = darkGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        octoberDays.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEach { day ->
                    CalendarDayCell(
                        day = day,
                        selected = day.number == selectedDay &&
                                day.isCurrentMonth,
                        onClick = {
                            onDaySelected(day.number)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
        }

        CalendarLegend()
    }
}


@Composable
private fun CareTaskCard(
    task: CareTask,
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

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
                .size(36.dp)
                .background(
                    color = Color(0xFFDDF2E5),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(task.icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF52755C)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.plantName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = task.description,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (completed) {
                        darkGreen
                    } else {
                        Color(0xFFDDF2E5)
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
private fun DailyTasksSection(
    onAddTaskClick: () -> Unit,
    onTaskClick: (CareTask) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Viernes, 18 de Octubre",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Text(
                    text = "3 necesidades pendientes para hoy",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier
                    .clickable(onClick = onAddTaskClick)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Text(
                    text = "Añadir",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        careTasks.forEach { task ->
            CareTaskCard(
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
fun CalendarMonthScreen(
    onWeekViewClick: () -> Unit = {},
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onTaskClick: (CareTask) -> Unit = {}
) {
    val darkGreen = Color(0xFF123D2B)
    val backgroundColor = Color(0xFFEAF9F0)

    var selectedFilter by remember {
        mutableStateOf(CalendarFilter.ALL)
    }

    var selectedDay by remember {
        mutableStateOf(18)
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
                        .clickable(onClick = onPreviousMonthClick)
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Octubre\n2026",
                    fontSize = 25.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkGreen
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "›",
                    fontSize = 30.sp,
                    color = darkGreen,
                    modifier = Modifier
                        .clickable(onClick = onNextMonthClick)
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CalendarViewSelector(
                    selectedView = CalendarView.MONTH,
                    onViewChange = { view ->
                        if (view == CalendarView.WEEK) {
                            onWeekViewClick()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CalendarFilters(
                selectedFilter = selectedFilter,
                onFilterChange = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MonthCalendar(
                selectedDay = selectedDay,
                onDaySelected = {
                    selectedDay = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DailyTasksSection(
                onAddTaskClick = onAddTaskClick,
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
private fun CalendarMonthScreenPreview() {
    PlantcareTheme {
        CalendarMonthScreen()
    }
}