package plat.plantcare.app.collection.plantDetails.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import plat.plantcare.app.R
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.HealthStatus
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.color
import plat.plantcare.app.core.data.models.getAge
import plat.plantcare.app.core.data.models.getHealthStatus
import plat.plantcare.app.core.data.models.getRemainingDays
import plat.plantcare.app.core.data.models.icon
import plat.plantcare.app.core.data.models.label
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsEvent
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsState
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsViewModel

@Serializable
data class PlantDetailsDestination(
    val id: String
)

fun NavController.navigateToPlantDetailsDestination(
    id: String,
    navOptions: NavOptions? = null
) {
    navigate(PlantDetailsDestination(id), navOptions)
}

fun NavGraphBuilder.plantDetailsScreen(
    onBackClick: () -> Unit,
    onMakeDiagnostic: () -> Unit
) {
    composable<PlantDetailsDestination> {
        PlantDetailsRoute(
            onBackClick = onBackClick,
            onMakeDiagnostic = onMakeDiagnostic
        )
    }
}

@Composable
fun PlantDetailsRoute(
    onBackClick: () -> Unit,
    onMakeDiagnostic: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlantDetailsScreen(
        onBackClick = onBackClick,
        onMakeDiagnostic = onMakeDiagnostic,
        onEvent = viewModel::onEvent,
        state = state,
        modifier = modifier
    )
}

//TODO: change ?: use for new state, in case something didn't load
//TODO: split states into their own files
@Composable
private fun PlantDetailsScreen(
    onBackClick: () -> Unit,
    onMakeDiagnostic: () -> Unit,
    onEvent: (PlantDetailsEvent) -> Unit,
    state: PlantDetailsState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "Detalles",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { onEvent(PlantDetailsEvent.ToggleFavorite) }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (state.plant?.favorite ?: false) R.drawable.favorites_full else R.drawable.favorites),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = { onEvent(PlantDetailsEvent.ToggleEditionMode) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = { onEvent(PlantDetailsEvent.ClickMoreOptions)}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.vertical_dots),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 32.dp
            )
        ) {
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
                            text = state.plant?.name ?: "Indefinido",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        StatusChip(state.diagnostic?.getHealthStatus() ?: HealthStatus.MUERTA)
                    }

                    Text(
                        text = state.reference?.scientificName ?: "Indefinido",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            item {
                PlantHero(
                    image = state.plant?.imageUrl ?: "",
                    location = state.plant?.location ?: "",
                    age = state.plant?.birthDate?.getAge() ?: "",
                    nextTask = state.nextTask,
                    onTaskCompletion = { onEvent(PlantDetailsEvent.CompleteTask(state.nextTask?.id ?: "")) }
                )
            }


            item {
                AccordionSection(
                    icon = R.drawable.filters,
                    title = "Características y requisitos",
                    //TODO: add how parameters are considered optimal
                    description = "6 parámetros óptimos"
                ) {
                    CharacteristicsContent(
                        characteristics = state.characteristics!!
                    )
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.monitor,
                    title = "Diagnóstico",
                    description = "Hace ${state.diagnostic?.date?.getRemainingDays() ?: 0} días"
                ) {
                    DiagnosisContent(
                        diagnosis = state.diagnostic!!,
                        onCreate = onMakeDiagnostic
                    )
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.checklist,
                    title = "Pautas de cuidado",
                    //TODO: update
                    description = ""
                ) {
                    CareGuidelinesContent(
                        guidelines = state.considerations!!
                    )
                }
            }

            item {
                AccordionSection(
                    icon = R.drawable.history,
                    title = "Historial de cuidados",
                    //TODO: update
                    description = "5 eventos completados este mes"
                ) {
                    CareHistoryContent(
                        historyList = state.history!!
                    )
                }
            }
        }
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
    image: String,
    location: String,
    age: String,
    nextTask: Task?,
    onTaskCompletion: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
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

        if(nextTask != null) {
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
                            text = "Próximo ${nextTask.action.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "En ${nextTask.dueDate.getRemainingDays()} días",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable(onClick = onTaskCompletion),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(nextTask.action.iconId),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = nextTask.action.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
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

@Composable
private fun CharacteristicsContent(
    characteristics: PlantCharacteristics,
    modifier: Modifier = Modifier
) {
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
private fun DiagnosisContent(
    diagnosis: Diagnostic,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
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
                //TODO: update
                StatusPill("Excelente")
            }
            Text(diagnosis.foliageState,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DiagnosisStat(
            R.drawable.bug,
            "Control plagas",
            diagnosis.pestControl,
            Modifier.weight(1f)
        )

        DiagnosisStat(
            R.drawable.power_leaf,
            "Análisis de vigor",
            diagnosis.growthVigor,
            Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row{
                PlaceholderIcon(icon = R.drawable.star, size = 16.dp)
                Spacer(Modifier.width(6.dp))
                Text("Último escaneo IA:", style = MaterialTheme.typography.labelSmall)
            }

            Text(
                text = "Hace ${diagnosis.date.getRemainingDays()} días",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Button(
            onClick = onCreate,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painterResource(id = R.drawable.camera),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text("Escanear nueva foto con IA")
        }
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
private fun CareGuidelinesContent(
    guidelines: PlantReferenceConsiderations,
    modifier: Modifier = Modifier
) {
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
private fun CareHistoryContent(
    historyList: List<Task>,
    modifier: Modifier = Modifier
) {
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
            .background(darkenColor(MaterialTheme.colorScheme.primary), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun PlaceholderIcon(
    icon: Int,
    size: Dp,
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