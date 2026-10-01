package plat.plantcare.app.collection

//TODO: change material3.* to specific imports
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.core.FullAppScaffold
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.core.lightenColor

enum class Tasks(val icon: Int) {
    WATERING(R.drawable.water_drop),
    SUNLIGHT(R.drawable.sunny),
    CLEANING(R.drawable.brush)
}

data class CollectionCardInfo(
    val name: String,
    val image: Int,
    val location: String,
    val tasks: List<Pair<Tasks, Boolean>> = emptyList()
)

private fun sampleTasks(active: Int) =
    Tasks.entries.mapIndexed { index, task -> task to (index == active) }

private val mockCollectionInfo = listOf(
    CollectionCardInfo("Monty", R.drawable.plant_img_example, "Salón", sampleTasks(0)),
    CollectionCardInfo("Espada", R.drawable.plant_img_example, "Dormitorio", sampleTasks(1)),
    CollectionCardInfo("Diva", R.drawable.plant_img_example, "Estudio", sampleTasks(2)),
    CollectionCardInfo("Cascada", R.drawable.plant_img_example, "Cocina", sampleTasks(0)),
    CollectionCardInfo("Rubí", R.drawable.plant_img_example, "Salón", sampleTasks(1)),
    CollectionCardInfo("Monedita", R.drawable.plant_img_example, "Balcón", sampleTasks(2))
)

@Composable
fun CollectionRoute() = CollectionScreen(mockCollectionInfo)

@Composable
private fun CollectionScreen(
    collection: List<CollectionCardInfo>,
    modifier: Modifier = Modifier
) {
    FullAppScaffold(section = "Colección") {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fullWidthItem { CollectionHeader(collection.size) }
            fullWidthItem { SearchBar() }
            fullWidthItem {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MOSTRANDO ${collection.size} REGISTRADAS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CollectionLayoutSelector(
                        selectedLayout = CollectionLayout.Grid,
                        onLayoutSelected = {}
                    )
                }
            }
            items(collection) { CollectionCard(it) }
            fullWidthItem { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) { content() }
}

@Composable
private fun CollectionHeader(plantCount: Int) {
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
                label = "Hoy", "2 por\nregar", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.sunny,
                label = "Clima", "22°C\nÓptimo", Modifier.weight(1f)
            )
            StatisticCard(
                icon = R.drawable.lotus,
                label = "Salud", "98%\nFeliz", Modifier.weight(1f)
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
private fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = "",
            onValueChange = {},
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        SmallActionButton(
            icon = R.drawable.add,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun SmallActionButton(icon: Int, containerColor: Color, contentColor: Color) {
    IconButton(
        onClick = {},
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
                    Modifier.fillMaxSize(),
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
                            shape = CircleShape)
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    data.tasks.forEach { (task, active) ->
                        Icon(
                            painterResource(id = task.icon),
                            contentDescription = null,
                            Modifier
                                .size(28.dp)
                                .background(
                                    color = if (active) {
                                        MaterialTheme.colorScheme.background
                                    } else {
                                        lightenColor(
                                            originalColor = MaterialTheme.colorScheme.background,
                                            factor = .2f)
                                    },
                                    shape = CircleShape
                                )
                                .padding(7.dp),
                            tint = if (active) {
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
                    color = MaterialTheme.colorScheme.primary, CircleShape)
        )
        Text(
            location,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

enum class CollectionLayout { Grid, List }

@Composable
fun CollectionLayoutSelector(
    selectedLayout: CollectionLayout,
    onLayoutSelected: (CollectionLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        CollectionLayout.Grid to R.drawable.grid_view,
        CollectionLayout.List to R.drawable.list_view
    )

    Row(
        modifier = modifier
            .size(width = 60.dp, height = 32.dp)
            .background(
                color = darkenColor(originalColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(8.dp))
            .padding(2.dp)
            .selectableGroup()
    ) {
        options.forEach { (layout, icon) ->
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
                        onClick = { onLayoutSelected(layout) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PreviewCollectionScreen() = CollectionScreen(mockCollectionInfo)

@Preview(showBackground = true)
@Composable
private fun PreviewCollectionCard() = CollectionCard(mockCollectionInfo.first())