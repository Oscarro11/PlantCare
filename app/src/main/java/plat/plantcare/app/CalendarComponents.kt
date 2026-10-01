package plat.plantcare.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CalendarView {
    MONTH,
    WEEK
}

enum class CalendarFilter {
    ALL,
    WATER,
    NUTRITION
}

@Composable
fun CalendarViewSelector(
    selectedView: CalendarView,
    onViewChange: (CalendarView) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)
    val lightGreen = Color(0xFFDDF2E5)

    Row(
        modifier = Modifier
            .background(
                color = lightGreen,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(3.dp)
    ) {
        CalendarViewOption(
            text = "Mes",
            selected = selectedView == CalendarView.MONTH,
            onClick = {
                onViewChange(CalendarView.MONTH)
            }
        )

        CalendarViewOption(
            text = "Semana",
            selected = selectedView == CalendarView.WEEK,
            onClick = {
                onViewChange(CalendarView.WEEK)
            }
        )
    }
}

@Composable
private fun CalendarViewOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Box(
        modifier = Modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 18.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            maxLines = 1,
            color = if (selected) Color.White else darkGreen
        )
    }
}

@Composable
fun CalendarFilterChip(
    text: String,
    @DrawableRes icon: Int? = null,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)
    val lightGreen = Color(0xFFDDF2E5)

    Row(
        modifier = Modifier
            .background(
                color = if (selected) darkGreen else lightGreen,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selected) {
                    Color.White
                } else {
                    Color(0xFF52755C)
                }
            )
        }

        Text(
            text = text,
            fontSize = 11.sp,
            color = if (selected) Color.White else darkGreen
        )
    }
}

