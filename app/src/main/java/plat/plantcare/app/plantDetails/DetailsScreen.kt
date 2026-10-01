package plat.plantcare.app.plantDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.core.FullAppScaffold
import plat.plantcare.app.core.darkenColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate

enum class HealthStatus {
    SALUDABLE,
    NECESITA_CUIDADO,
    EN_PELIGRO,
    MUERTA
}

private fun HealthStatus.icon(): Int = when (this) {
    HealthStatus.SALUDABLE -> R.drawable.verified_24dp_000000_fill0_wght400_grad0_opsz24
    HealthStatus.NECESITA_CUIDADO -> R.drawable.waving_hand
    HealthStatus.EN_PELIGRO -> R.drawable.danger
    HealthStatus.MUERTA -> R.drawable.blocked
}

private fun HealthStatus.label(): String = when (this) {
    HealthStatus.SALUDABLE -> "Saludable"
    HealthStatus.NECESITA_CUIDADO -> "Necesita cuidado"
    HealthStatus.EN_PELIGRO -> "En peligro"
    HealthStatus.MUERTA -> "Muerta"
}

@Composable
private fun HealthStatus.color(): Color = when (this) {
    HealthStatus.SALUDABLE -> MaterialTheme.colorScheme.primary
    HealthStatus.NECESITA_CUIDADO -> Color(0xFFFFA000)
    HealthStatus.EN_PELIGRO -> MaterialTheme.colorScheme.error
    HealthStatus.MUERTA -> Color.Gray
}

@Composable
fun DetailsRoute(id: Int) {
    DetailsScreen(
        name = "Monty",
        status = HealthStatus.SALUDABLE,
        scientificName = "Monstera Deliciosa · Costilla de Adán",
        image = R.drawable.plant_img_example,
        location = "Salón",
        age = "1 año y 4 meses",
        nextWatering = "En 2 días (Sábado)"
    )
}

@Composable
private fun DetailsScreen(
    name: String,
    status: HealthStatus,
    scientificName: String,
    image: Int,
    location: String,
    age: String,
    nextWatering: String,
    modifier: Modifier = Modifier
) {
    FullAppScaffold(section = "Colección") {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 32.dp
            )
        ) {
            item { DetailsToolbar() }

            item {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        StatusChip(status)
                    }

                    Text(
                        text = scientificName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            item {
                PlantHero(
                    image = image,
                    location = location,
                    age = age,
                    nextWatering = nextWatering
                )
            }

            item {
                AccordionSection(
                    icon = R.drawable.filters,
                    title = "Características y requisitos",
                    description = "6 parámetros óptimos"
                ) {
                    CharacteristicsContent()
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.monitor,
                    title = "Diagnóstico",
                    description = "Hace 3 días - Botanika IA"
                ) {
                    DiagnosisContent()
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.checklist,
                    title = "Pautas de cuidado",
                    description = "Adaptadas al microclima del $location"
                ) {
                    CareGuidelinesContent()
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.history,
                    title = "Historial de cuidados",
                    description = "5 eventos completados este mes"
                ) {
                    CareHistoryContent()
                }
            }
        }
    }
}

@Composable
private fun DetailsToolbar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Colección",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircleAction(R.drawable.favorites_full)
            CircleAction(R.drawable.edit)
            CircleAction(R.drawable.vertical_dots)
        }
    }
}

@Composable
private fun CircleAction(icon: Int) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = darkenColor(originalColor = MaterialTheme.colorScheme.background), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StatusChip(status: HealthStatus) {
    val color = status.color()

    Row(
        modifier = Modifier
            .background(color.copy(alpha = .2f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(status.icon()),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = darkenColor(color, .2f)
        )
        Text(
            text = status.label(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = darkenColor(color, .2f)
        )
    }
}

@Composable
private fun PlantHero(
    image: Int,
    location: String,
    age: String,
    nextWatering: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(
                icon = R.drawable.location,
                text = location
            )
            InfoChip(
                icon = R.drawable.filled_calendar,
                text = age
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = .92f),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(darkenColor(MaterialTheme.colorScheme.background, .05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.water_drop
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Próximo Riego",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = nextWatering,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.water_drop
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Regar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun InfoChip(icon: Int, text: String) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = .9f),
                CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun AccordionSection(
    icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = darkenColor(MaterialTheme.colorScheme.background),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }

            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    content = content
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PreviewDetailsScreen() {
    DetailsScreen(
        name = "Monty",
        status = HealthStatus.SALUDABLE,
        scientificName = "Monstera Deliciosa · Costilla de Adán",
        image = R.drawable.plant_img_example,
        location = "Salón",
        age = "1 año y 4 meses",
        nextWatering = "En 2 días (Sábado)"
    )
}

@Composable
private fun CharacteristicsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ParameterCard(
                label = "Luz requerida",
                value = "Brillante indirecta",
                level = 3,
                icon = R.drawable.sunny,
                modifier = Modifier.weight(1f)
            )
            ParameterCard(
                label = "Riego",
                value = "Moderado (6–8d)",
                detail = "2/4",
                level = 2,
                icon = 0,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ParameterCard(
                label = "Humedad",
                value = "Alta (60–70%)",
                detail = "3/4",
                level = 3,
                icon = 0,
                modifier = Modifier.weight(1f)
            )
            ParameterCard(
                label = "Clima",
                value = "18°C – 27°C",
                supportingText = "Templado cálido",
                icon = R.drawable.thermostat,
                modifier = Modifier.weight(1f)
            )
        }

        RequirementRow(
            icon = R.drawable.landscape,
            label = "Sustrato:",
            value = "Aireado con perlita y fibra"
        )
        RequirementRow(
            icon = R.drawable.potted_plant,
            label = "Maceta y tamaño:",
            value = "Barro terracota con drenaje",
            trailingText = "Grande · 26cm"
        )
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
                PlaceholderIcon(icon = icon, size = 15.dp)
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
    icon: Int,
    label: String,
    value: String,
    trailingText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceholderIcon(icon = icon, size = 16.dp)
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

@Composable
private fun DiagnosisContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkenColor(MaterialTheme.colorScheme.background, 0.05f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlaceholderIcon(icon = R.drawable.check_circle, size = 16.dp)
                Spacer(Modifier.width(5.dp))
                Text("Estado del follaje", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                StatusPill("Excelente")
            }
            Text(
                "Hojas limpias y sin manchas, color verde intenso con fenestraciones definidas. " +
                        "Transpiración y desarrollo foliar óptimos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DiagnosisStat(R.drawable.bug, "Control plagas", "Sin signos ni ácaros", Modifier.weight(1f))
            DiagnosisStat(R.drawable.power_leaf, "Análisis de vigor", "2 brotes nuevos", Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            PlaceholderIcon(icon = R.drawable.star, size = 16.dp)
            Spacer(Modifier.width(6.dp))
            Text("Último escaneo IA:", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(6.dp))
            Text("Hace 3 días · Puntaje 9.8/10", style = MaterialTheme.typography.labelSmall)
        }

        DarkAction(text = "Escanear nueva foto con IA", icon = R.drawable.camera)
    }
}

@Composable
private fun DiagnosisStat(icon: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceholderIcon(icon = icon, size = 16.dp)
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CareGuidelinesContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CareRow(R.drawable.water_drop, "Riego", "Espera a que los primeros 3–5 cm de sustrato estén secos. Usa agua templada declorada.")
        CareRow(R.drawable.sunny, "Ubicación y luz", "Mantener a 1.5 metros de la ventana del salón, evitando sol directo en horas centrales.")
        CareRow(R.drawable.landscape, "Nutrición", "Abonar con fertilizante equilibrado de hojas verdes una vez al mes durante primavera y verano.")
        CareRow(R.drawable.brush, "Pulverización y limpieza", "Pulverizar el follaje 2 veces por semana para emular humedad tropical y limpiar polvo de las hojas con paño húmedo.")
    }
}

@Composable
private fun CareRow(icon: Int, title: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(darkenColor(MaterialTheme.colorScheme.background, 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            PlaceholderIcon(icon = icon, size = 15.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CareHistoryContent() {
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        HistoryRow(R.drawable.water_drop, "Riego abundante y drenaje", "Completado con 750ml", "Ayer, 10:30 AM")
        HistoryRow(R.drawable.brush, "Limpieza de follaje y abrillantado", "Con paño húmedo y aceite de neem", "Hace 5 días")
        HistoryRow(R.drawable.interchange, "Rotación de maceta 90°", "Para crecimiento homogéneo hacia la luz", "Hace 8 días")
        HistoryRow(R.drawable.leaf, "Fertilización líquida suave", "Dosis recomendada", "Hace 16 días")
        Spacer(Modifier.height(2.dp))
        DarkAction(text = "Registrar nuevo evento", icon = R.drawable.add)
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
            PlaceholderIcon(icon = icon, size = 11.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun StatusPill(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun DarkAction(text: String, icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceholderIcon(icon = icon, size = 15.dp, tint = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.width(7.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun PlaceholderIcon(
    icon: Int,
    size: androidx.compose.ui.unit.Dp,
    tint: Color = MaterialTheme.colorScheme.onBackground
) {
    if (icon != 0) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .border(1.dp, tint.copy(alpha = .45f), CircleShape)
        )
    }
}