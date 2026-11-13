package plat.plantcare.app.collection.plantDetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.getRemainingDays
import plat.plantcare.app.core.ui.AccordionSection
import plat.plantcare.app.core.ui.darkenColor

@Composable
fun HistorySection(
    description: String,
    history: List<Task>?,
    modifier: Modifier = Modifier
) {
    AccordionSection(
        icon = R.drawable.history,
        title = "Historial de cuidados",
        description = description,
        isEnabled = (history?.size ?: 0) > 0
    ) {
        CareHistoryContent(
            historyList = history,
            modifier = modifier
        )
    }
}

@Composable
private fun CareHistoryContent(
    historyList: List<Task>?,
    modifier: Modifier = Modifier
) {
    if (historyList != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(11.dp),
            modifier = modifier
        ) {
            historyList
                .takeLast(5)
                .reversed()
                .forEach { task ->
                    HistoryRow(
                        icon = task.action.iconId,
                        title = task.action.displayName,
                        description = task.description,
                        date = "Hace ${task.dueDate.getRemainingDays()} días"
                    )
                }
        }
    }

}

@Composable
private fun HistoryRow(icon: Int, title: String, description: String, date: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(darkenColor(MaterialTheme.colorScheme.background, 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(11.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}