package plat.plantcare.app.collection.plantDetails.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.ui.AccordionSection
import plat.plantcare.app.core.ui.darkenColor

@Composable
fun CharacteristicsSection(
    description: String,
    characteristics: PlantCharacteristics?,
    modifier: Modifier = Modifier
) {
    AccordionSection(
        icon = R.drawable.filters,
        title = "Características y requisitos",
        description = description,
        isEnabled = characteristics != null
    ) {
        CharacteristicsContent(
            characteristics = characteristics,
            modifier = modifier
        )
    }
}

@Composable
private fun CharacteristicsContent(
    characteristics: PlantCharacteristics?,
    modifier: Modifier = Modifier
) {
    if (characteristics != null) {

        //TODO: change this to Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParameterCard(
                    label = "Luz requerida",
                    value = characteristics.requiredLight.displayName,
                    level = characteristics.requiredLight.level,
                    icon = R.drawable.sunny,
                    modifier = Modifier.weight(1f)
                )
                ParameterCard(
                    label = "Riego",
                    value = characteristics.wateringFrequencyDays.displayName,
                    detail = "${characteristics.wateringFrequencyDays.level}/4",
                    level = characteristics.wateringFrequencyDays.level,
                    icon = 0,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParameterCard(
                    label = "Humedad",
                    value = characteristics.humidityLevel.displayName,
                    detail = "${characteristics.humidityLevel.level}/4",
                    level = characteristics.humidityLevel.level,
                    icon = 0,
                    modifier = Modifier.weight(1f)
                )
                ParameterCard(
                    label = "Clima",
                    value = characteristics.preferredClimate,
                    //TODO: update
                    supportingText = "Templado cálido",
                    icon = R.drawable.thermostat,
                    modifier = Modifier.weight(1f)
                )
            }

            RequirementRow(
                icon = R.drawable.landscape,
                label = "Sustrato:",
                value = characteristics.substrate
            )
            RequirementRow(
                icon = R.drawable.potted_plant,
                label = "Maceta y tamaño:",
                value = characteristics.pot,
                //TODO: update
                trailingText = "Grande · 26cm"
            )
        }
    }

}

@Composable
private fun ParameterCard(
    label: String,
    value: String,
    icon: Int,
    modifier: Modifier = Modifier,
    detail: String? = null,
    supportingText: String? = null,
    level: Int = 0
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (detail != null) {
                Text(detail, style = MaterialTheme.typography.labelSmall)
            } else {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        if (supportingText != null) {
            Text(
                supportingText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (level > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(4) { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (index < level) MaterialTheme.colorScheme.primary
                                else darkenColor(MaterialTheme.colorScheme.background),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun RequirementRow(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    trailingText: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }
}