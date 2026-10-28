package plat.plantcare.app.collection.plantDetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsEvent
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsState
import plat.plantcare.app.collection.plantDetails.data.PlantDetailsViewModel
import plat.plantcare.app.core.data.models.HealthStatus
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.color
import plat.plantcare.app.core.data.models.getAge
import plat.plantcare.app.core.data.models.getHealthStatus
import plat.plantcare.app.core.data.models.getRemainingDays
import plat.plantcare.app.core.data.models.icon
import plat.plantcare.app.core.data.models.label
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.core.ui.darkenColor

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

@Composable
private fun PlantDetailsScreen(
    onBackClick: () -> Unit,
    onMakeDiagnostic: () -> Unit,
    onEvent: (PlantDetailsEvent) -> Unit,
    state: PlantDetailsState,
    modifier: Modifier = Modifier
) {
    when (state) {
        PlantDetailsState.Loading -> PlantDetailsLoading()

        PlantDetailsState.NotFound -> PlantDetailsError(
            onBackClick = onBackClick
        )

        is PlantDetailsState.Content -> PlantDetailsContent(
            onBackClick = onBackClick,
            onMakeDiagnostic = onMakeDiagnostic,
            onEvent = onEvent,
            state = state,
            modifier = modifier
        )
    }
}

@Composable
private fun PlantDetailsLoading(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()

        Text(
            text = "Cargando...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PlantDetailsError(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "No se pudo obtener los datos de esta planta",
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedButton(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Red
            )
        ) {
            Text(
                text = "Regresar",
                color = Color.Red
            )
        }
    }
}

@Composable
private fun PlantDetailsContent(
    onBackClick: () -> Unit,
    onMakeDiagnostic: () -> Unit,
    onEvent: (PlantDetailsEvent) -> Unit,
    state: PlantDetailsState.Content,
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
                                if (state.plant.favorite) R.drawable.favorites_full else R.drawable.favorites),
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
                            text = state.plant.name,
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
                PlantShowcase(
                    image = state.plant.imageUrl,
                    location = state.plant.location,
                    age = state.plant.birthDate?.getAge() ?: "",
                    nextTask = state.nextTask,
                    onTaskCompletion = { onEvent(PlantDetailsEvent.CompleteTask(state.nextTask?.id ?: "")) }
                )
            }

            item {
                CharacteristicsSection(
                    //TODO: ver que significa parámetros óptimos
                    description = "6 parámetros óptimos",
                    characteristics = state.characteristics
                )
            }

            item {
                DiagnosticSection(
                    description = "Hace ${state.diagnostic?.date?.getRemainingDays() ?: 0} días",
                    diagnostic = state.diagnostic,
                    onCreate = onMakeDiagnostic,
                )
            }

            item {
                CareGuidelinesSection(
                    description = "Adaptadas al microclima de ${state.plant.location}",
                    considerations = state.considerations
                )
            }

            item {
                HistorySection(
                    description = "5 eventos completados este mes",
                    history = state.history
                )
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
private fun PlantShowcase(
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