package plat.plantcare.app.collection.allPlants.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import plat.plantcare.app.collection.allPlants.data.CollectionCardInfo
import plat.plantcare.app.collection.allPlants.data.CollectionEvent
import plat.plantcare.app.collection.allPlants.data.CollectionLayout
import plat.plantcare.app.collection.allPlants.data.CollectionState
import plat.plantcare.app.collection.allPlants.data.CollectionViewModel
import plat.plantcare.app.core.ui.darkenColor
import plat.plantcare.app.core.ui.lightenColor
import kotlin.math.round

@Serializable data object CollectionDestination

fun NavController.navigateToCollectionDestination(navOptions: NavOptions? = null) {
    navigate(CollectionDestination, navOptions)
}

fun NavGraphBuilder.collectionScreen(
    onAddPlant: () -> Unit,
    onExaminePlant: (String) -> Unit,
) {
    composable<CollectionDestination> {
        CollectionRoute(
            onAddPlant = onAddPlant,
            onExaminePlant = onExaminePlant,
        )
    }
}

@Composable
fun CollectionRoute(
    onAddPlant: () -> Unit,
    onExaminePlant: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectionScreen(
        onAddPlant = onAddPlant,
        onExaminePlant = onExaminePlant,
        onFilterClick = { viewModel.onEvent(CollectionEvent.TogglePending) },
        onSearchQueryChange = { query -> viewModel.onEvent(CollectionEvent.ChangeSearchbar(query)) },
        onLayoutChange = { viewModel.onEvent(CollectionEvent.ToggleDisplay) },
        state = state,
        modifier = modifier
    )
}

@Composable
internal fun CollectionScreen(
    onAddPlant: () -> Unit,
    onExaminePlant: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLayoutChange: () -> Unit,
    state: CollectionState,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fullWidthItem {
            CollectionHeader(
                plantCount = state.totalPlants,
                pendingOfWatering = state.pendingWatering,
                climate = state.todayClimate,
                healthPercentage = state.healthPercentage
            )
        }

        fullWidthItem {
            SearchBar(
                queryValue = state.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                //TODO: add filter to query searchbar
                onFilterClick = onFilterClick,
                onAddPlant = onAddPlant
            )
        }

        fullWidthItem {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MOSTRANDO ${state.plantList.size} DE ${state.totalPlants}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CollectionLayoutSelector(
                    selectedLayout = state.selectedLayout,
                    onLayoutSelected = onLayoutChange
                )
            }
        }

        state.preferencesError?.let { message ->
            fullWidthItem {
                Text(text = message, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        }

        if (state.loading) fullWidthItem { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (state.pendingOnly) fullWidthItem {
            InputChip(selected = true, onClick = onFilterClick, label = { Text("Cuidados pendientes · Quitar filtro") })
        }
        if (!state.loading && state.plantList.isEmpty()) fullWidthItem {
            Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painterResource(R.drawable.potted_plant), null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Text(if (state.totalPlants == 0) "Tu colección empieza con una planta" else "No hay plantas con estos filtros",
                    Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onBackground)
                if (state.totalPlants == 0) TextButton(onClick = onAddPlant) { Text("Agregar planta") }
            }
        }
        when (state.selectedLayout) {
            CollectionLayout.GRID -> {
                items(
                    items = state.plantList,
                    key = { it.id }
                ) { plant ->
                    CollectionCard(
                        data = plant,
                        onClick = { onExaminePlant(plant.id) }
                    )
                }
            }

            CollectionLayout.LIST -> {
                items(
                    items = state.plantList,
                    key = { it.id },
                    span = { GridItemSpan(maxLineSpan) }
                ) { plant ->
                    CollectionRow(
                        data = plant,
                        onClick = { onExaminePlant(plant.id) },
                        //TODO: check if more options are needed
                        onOptionsClick = { onExaminePlant(plant.id) }
                    )
                }
            }
        }

        fullWidthItem { Spacer(Modifier.height(24.dp)) }
    }
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) { content() }
}

@Composable
private fun CollectionHeader(
    plantCount: Int,
    pendingOfWatering: Int,
    climate: String,
    healthPercentage: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.forest),
                        contentDescription = null,
                        Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "TU OASIS VERDE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    "Mi Selva\nUrbana",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .shadow(1.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    plantCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text("plantas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticCard(
                icon = R.drawable.water_drop,
                label = "Pendientes", "$pendingOfWatering por\nregar", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.sunny,
                label = "Clima", "Sin datos", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.lotus,
                label = "Al día", "${(healthPercentage*100).toInt()}%\nSin atrasos", Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatisticCard(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(min = 66.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(icon), contentDescription = null,
                modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SearchBar(
    queryValue: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onAddPlant: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = queryValue,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            placeholder = {
                Text(
                    "Nombre o ubicación",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.search),
                    contentDescription = null,
                    Modifier.size(18.dp)
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        SmallActionButton(
            icon = R.drawable.filters,
            onClick = onFilterClick,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        SmallActionButton(
            icon = R.drawable.add,
            onClick = onAddPlant,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun SmallActionButton(
    icon: Int,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = if (icon == R.drawable.add) "Agregar planta" else "Filtrar por cuidados pendientes",
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun CollectionCard(data: CollectionCardInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Box(Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
            if (data.image.isNullOrBlank()) {
                Icon(painterResource(R.drawable.potted_plant), null,
                    Modifier.size(48.dp).align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                AsyncImage(model = data.image, contentDescription = data.name, contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.potted_plant), modifier = Modifier.fillMaxSize())
            }
            if (data.location.isNotBlank()) LocationChip(data.location, Modifier.align(Alignment.TopStart).padding(8.dp))
        }
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(data.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
            Text(if (data.tasks.isEmpty()) "Cuidados al día" else "${data.tasks.size} cuidados pendientes",
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (data.tasks.isNotEmpty()) LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(data.tasks) { task ->
                    Icon(painterResource(task.action.iconId), task.action.displayName,
                        Modifier.size(28.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape).padding(6.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun CollectionRow(
    data: CollectionCardInfo,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (data.image.isNullOrBlank()) {
                    Icon(painterResource(R.drawable.potted_plant), contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    AsyncImage(model = data.image, contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(data.location, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(data.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (data.tasks.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(data.tasks) { task ->
                            Icon(painterResource(task.action.iconId), contentDescription = null,
                                modifier = Modifier.size(28.dp).background(
                                    color = if (task.completed) MaterialTheme.colorScheme.surfaceContainerHighest
                                        else MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ).padding(7.dp),
                                tint = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                    else MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
            IconButton(onClick = onOptionsClick) {
                Icon(painterResource(R.drawable.vertical_dots),
                    contentDescription = "Opciones de ${data.name}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LocationChip(location: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = .95f),
                shape = CircleShape
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(6.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary, CircleShape
                )
        )
        Text(
            location,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CollectionLayoutSelector(
    selectedLayout: CollectionLayout,
    onLayoutSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(width = 88.dp, height = 44.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(2.dp)
            .selectableGroup()
    ) {
        CollectionLayout.entries.forEach { layout ->
            val selected = selectedLayout == layout

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(if (selected) 1.dp else 0.dp, RoundedCornerShape(6.dp))
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = { if (!selected) onLayoutSelected() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(layout.icon),
                    contentDescription = if (layout == CollectionLayout.LIST) "Vista de lista" else "Vista de cuadrícula",
                    tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}