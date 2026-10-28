package plat.plantcare.app.home.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import plat.plantcare.app.core.data.models.backgroundColor
import plat.plantcare.app.core.data.models.contentColor
import plat.plantcare.app.core.ui.IconWithBackground
import plat.plantcare.app.core.ui.darkenColor
import plat.plantcare.app.core.ui.lightenColor
import plat.plantcare.app.home.home.data.HomeEvent
import plat.plantcare.app.home.home.data.HomeState
import plat.plantcare.app.home.home.data.HomeTaskCardUiState
import plat.plantcare.app.home.home.data.HomeViewModel
import kotlin.math.round

@Serializable data object HomeDestination

fun NavController.navigateToHomeDestination(navOptions: NavOptions? = null) {
    navigate(HomeDestination, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onSeeAll: () -> Unit,
    onDiagnosis: () -> Unit,
    onAddPlant: () -> Unit
) {
    composable<HomeDestination> {
        HomeRoute(
            onSeeAll = onSeeAll,
            onDiagnosis = onDiagnosis,
            onAddPlant = onAddPlant
        )
    }
}

@Composable
fun HomeRoute(
    onSeeAll: () -> Unit,
    onDiagnosis: () -> Unit,
    onAddPlant: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        onSeeAll = onSeeAll,
        onTaskCompletion = { id -> viewModel.onEvent(HomeEvent.CompleteTask(id)) },
        onDiagnosis = onDiagnosis,
        onAddPlant = onAddPlant,
        state = state,
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    onSeeAll: () -> Unit,
    onTaskCompletion: (String) -> Unit,
    onDiagnosis: () -> Unit,
    onAddPlant: () -> Unit,
    state: HomeState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 18.dp,
            top = 18.dp,
            end = 18.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderSummary( state = state )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "¡Buenos días, ${state.userName}!",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Hoy tienes ${state.remainingPlants} plantas esperando tu toque verde",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            DailyProgressCard(
                pendingTasks = state.pendingTasks,
                totalTasks = state.tasks.size
            )
        }

        item {
            TasksHeader(
                onSeeAll = onSeeAll
            )
        }

        items(
            items = state.tasks
        ) { item ->
            TaskCard(
                state = item,
                onCompletion = onTaskCompletion,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            QuickActions(
                onDiagnosis = onDiagnosis,
                onAddPlant = onAddPlant
            )
        }
    }
}

@Composable
private fun HeaderSummary(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = state.date.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        IconWithBackground(
            icon = R.drawable.potted_plant,
            size = 36.dp,
            backgroundColor = darkenColor(
                originalColor = MaterialTheme.colorScheme.background,
                factor = 0.2f
            ),
            backgroundShape = RoundedCornerShape(18.dp),
            tintColor = MaterialTheme.colorScheme.onBackground
        ) {
            Text(
                text = "${state.plantTotal} plantas activas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun DailyProgressCard(
    pendingTasks: Int,
    totalTasks: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        val progressPercentage: Float = if (totalTasks > 0) 1f - (pendingTasks.toFloat() / totalTasks) else 1f

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ritual de hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$pendingTasks de $totalTasks completadas (${round(progressPercentage * 100)}%)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { progressPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
                gapSize = 0.dp
            )
        }
    }
}

@Composable
private fun TasksHeader(
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tareas de hoy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onSeeAll)
        ) {
            Text(
                text = "Ver todas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                painter = painterResource(R.drawable.arrow_forward),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TaskCard(
    state: HomeTaskCardUiState,
    onCompletion: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: Int? = 2
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = state.plantImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.urgency.name,
                        modifier = Modifier
                            .background(
                                color = state.urgency.backgroundColor(),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = state.urgency.contentColor()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                R.drawable.location
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = state.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = state.plantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${state.action.displayName ?: ""} - ${state.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = size ?: Int.MAX_VALUE
                )
            }

            Spacer(Modifier.width(8.dp))

            IconWithBackground(
                icon = if (state.completed) R.drawable.check else state.action.iconId,
                size = 40.dp,
                backgroundColor = darkenColor(
                    originalColor = MaterialTheme.colorScheme.background,
                    factor = 0.2f
                ),
                backgroundShape = CircleShape,
                tintColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clickable( onClick = { onCompletion(state.id) } )
            )
        }
    }
}

@Composable
private fun QuickActions(
    onDiagnosis: () -> Unit,
    onAddPlant: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionCard(
            icon = R.drawable.camera,
            title = "Diagnosticar",
            description = "Escanear hoja",
            color = MaterialTheme.colorScheme.surface,
            fontColor = MaterialTheme.colorScheme.onSurface,
            iconBackground = MaterialTheme.colorScheme.background,
            iconTint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(onClick = onDiagnosis)
        )

        ActionCard(
            icon = R.drawable.add_circle,
            title = "Añadir planta",
            description = "A tu colección",
            color = MaterialTheme.colorScheme.primary,
            fontColor = MaterialTheme.colorScheme.onPrimary,
            iconBackground = lightenColor(MaterialTheme.colorScheme.primary),
            iconTint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(onClick = onAddPlant)
        )
    }
}

@Composable
fun ActionCard(
    icon: Int,
    title: String,
    description: String?,
    color: Color,
    fontColor: Color,
    iconBackground: Color,
    iconTint: Color?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IconWithBackground(
                icon = icon,
                size = 48.dp,
                backgroundColor = iconBackground,
                backgroundShape = CircleShape,
                tintColor = iconTint
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = fontColor
            )

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = fontColor
                )
            }
        }
    }
}

@Preview(
    name = "Home screen",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onSeeAll = {},
        onTaskCompletion = {},
        onDiagnosis = {},
        onAddPlant = {},
        state = HomeState()
    )
}

@Preview(showBackground = true)
@Composable
private fun ActionCardPreview() {
    ActionCard(
        icon = R.drawable.camera,
        title = "Diagnosticar",
        description = "Escanear hoja",
        color = Color.White,
        fontColor = Color.Black,
        iconBackground = Color.Green,
        iconTint = null,
        modifier = Modifier.size(width = 190.dp, height = 150.dp)
    )
}