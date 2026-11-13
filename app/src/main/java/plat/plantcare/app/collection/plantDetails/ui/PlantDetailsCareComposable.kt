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
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations
import plat.plantcare.app.core.ui.AccordionSection
import plat.plantcare.app.core.ui.darkenColor

@Composable
fun CareGuidelinesSection(
    description: String,
    considerations: PlantReferenceConsiderations?,
    modifier: Modifier = Modifier
) {
    AccordionSection(
        icon = R.drawable.checklist,
        title = "Pautas de cuidado",
        description = description,
        isEnabled = considerations != null
    ) {
        CareGuidelinesContent(
            guidelines = considerations,
            modifier = modifier
        )
    }
}

@Composable
private fun CareGuidelinesContent(
    guidelines: PlantReferenceConsiderations?,
    modifier: Modifier = Modifier
) {
    if (guidelines != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
        ) {
            CareRow(
                R.drawable.water_drop,
                "Riego",
                guidelines.wateringGuide
            )
            CareRow(
                R.drawable.sunny,
                "Ubicación y luz",
                guidelines.recommendedLocationAndLight
            )
            CareRow(
                R.drawable.landscape,
                "Nutrición",
                guidelines.nutrition
            )
            CareRow(
                R.drawable.brush,
                "Pulverización y limpieza",
                guidelines.sprayingAndCleaning
            )
        }
    }
}

@Composable
private fun CareRow(icon: Int, title: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(darkenColor(MaterialTheme.colorScheme.background, 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}