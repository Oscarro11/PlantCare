package plat.plantcare.app.collection.allPlants.ui

//TODO: change material3.* to specific imports
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.R
import plat.plantcare.app.collection.allPlants.data.CollectionCardInfo
import plat.plantcare.app.collection.allPlants.data.CollectionEvent
import plat.plantcare.app.collection.allPlants.data.CollectionLayout
import plat.plantcare.app.collection.allPlants.data.CollectionState
import plat.plantcare.app.collection.allPlants.data.CollectionViewModel
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.core.lightenColor
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
        onFilterClick = {},
        onSearchQueryChange = { query -> viewModel.onEvent(CollectionEvent.ChangeSearchbar(query)) },
        onLayoutChange = { viewModel.onEvent(CollectionEvent.ToggleDisplay) },
        state = state,
        modifier = modifier
    )
}

@Composable
private fun CollectionScreen(
    onAddPlant: () -> Unit,
    onExaminePlant: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLayoutChange: () -> Unit,
    state: CollectionState,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fullWidthItem {
            CollectionHeader(
                plantCount = state.plantList.size,
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
                    text = "MOSTRANDO ${state.plantList.size} REGISTRADAS",
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
                        onOptionsClick = {}
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
                color = darkenColor(originalColor = MaterialTheme.colorScheme.background)
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
                    fontWeight = FontWeight.SemiBold
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
                    fontWeight = FontWeight.SemiBold
                )
                Text("plantas", style = MaterialTheme.typography.labelSmall)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticCard(
                icon = R.drawable.water_drop,
                label = "Hoy", "$pendingOfWatering por\nregar", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.sunny,
                label = "Clima", "$climate\nÓptimo", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.lotus,
                label = "Salud", "${round(healthPercentage*100)}%\nFeliz", Modifier.weight(1f)
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
    Row(
        modifier = modifier
            .height(66.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = .8f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
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
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            placeholder = {
                Text(
                    "Buscar por nombre,\napodo...",
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
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun CollectionCard(
    data: CollectionCardInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(172.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                Image(
                    painterResource(id = data.image),
                    contentDescription = null,
                    Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClick),
                    contentScale = ContentScale.Crop
                )
                LocationChip(
                    data.location,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
                Icon(
                    painterResource(id = R.drawable.vertical_dots),
                    contentDescription = null,
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = .85f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    data.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = data.tasks
                    ) { task ->
                        Icon(
                            painterResource(id = task.action.iconId),
                            contentDescription = null,
                            Modifier
                                .size(28.dp)
                                .background(
                                    color = if (!task.completed) {
                                        MaterialTheme.colorScheme.background
                                    } else {
                                        lightenColor(
                                            originalColor = MaterialTheme.colorScheme.background,
                                            factor = .2f
                                        )
                                    },
                                    shape = CircleShape
                                )
                                .padding(7.dp),
                            tint = if (!task.completed) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                lightenColor(originalColor = MaterialTheme.colorScheme.onBackground, factor = .5f)
                            }
                        )
                    }
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            //TODO: change Image to Coil Image
            Image(
                painterResource(id = data.image),
                contentDescription = null,
                Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClick),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = data.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = data.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = data.tasks
                ) { task ->
                    Icon(
                        painterResource(id = task.action.iconId),
                        contentDescription = null,
                        Modifier
                            .size(28.dp)
                            .background(
                                color = if (!task.completed) {
                                    MaterialTheme.colorScheme.background
                                } else {
                                    lightenColor(
                                        originalColor = MaterialTheme.colorScheme.background,
                                        factor = .2f
                                    )
                                },
                                shape = CircleShape
                            )
                            .padding(7.dp),
                        tint = if (!task.completed) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            lightenColor(originalColor = MaterialTheme.colorScheme.onBackground, factor = .5f)
                        }
                    )
                }
            }
        }

        Text(
            text = "⋮",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onOptionsClick)
                .padding(8.dp)
        )

        Text(
            text = "›",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun LocationChip(location: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = darkenColor(originalColor = MaterialTheme.colorScheme.background).copy(alpha = .9f),
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
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
            .size(width = 60.dp, height = 32.dp)
            .background(
                color = darkenColor(originalColor = MaterialTheme.colorScheme.background),
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
                        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = onLayoutSelected
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(layout.icon),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp))
            }
        }
    }
}