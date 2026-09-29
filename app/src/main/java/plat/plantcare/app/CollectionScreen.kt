package plat.plantcare.app


import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.core.FullAppScaffold


data class Plant(
    val name: String,
    val location: String,
    val statusIcons: List<Int>
)


private val plants = listOf(
    Plant(
        name = "Monty",
        location = "SALÓN",
        statusIcons = listOf(
            R.drawable.ic_water_filled,
            R.drawable.ic_sun_outline,
            R.drawable.ic_humidity
        )
    ),
    Plant(
        name = "Espada",
        location = "DORMITORIO",
        statusIcons = listOf(
            R.drawable.ic_water_outline,
            R.drawable.ic_light,
            R.drawable.ic_shield
        )
    ),
    Plant(
        name = "Diva",
        location = "ESTUDIO",
        statusIcons = listOf(
            R.drawable.ic_pot,
            R.drawable.ic_water_filled,
            R.drawable.ic_temperature
        )
    ),
    Plant(
        name = "Cascada",
        location = "COCINA",
        statusIcons = listOf(
            R.drawable.ic_water_filled,
            R.drawable.ic_shovel,
            R.drawable.ic_growth
        )
    ),
    Plant(
        name = "Rubí",
        location = "SALÓN",
        statusIcons = listOf(
            R.drawable.ic_water_outline,
            R.drawable.ic_pot,
            R.drawable.ic_sun_outline
        )
    ),
    Plant(
        name = "Monedita",
        location = "BALCÓN",
        statusIcons = listOf(
            R.drawable.ic_humidity,
            R.drawable.ic_rotation,
            R.drawable.ic_light
        )
    )
)


@Composable
fun PlantStatusIcon(
    @DrawableRes icon: Int
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(
                color = Color(0xFFDDF2E5),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = Color(0xFF52755C)
        )
    }
}


@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Placeholder preparado para reemplazarse posteriormente por Image.
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(0xFFDDEEE3),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌿",
                fontSize = 22.sp
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
                            color = darkGreen,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = plant.location,
                    fontSize = 8.sp,
                    color = darkGreen
                )
            }

            Text(
                text = plant.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                plant.statusIcons.forEach { icon ->
                    PlantStatusIcon(icon = icon)
                }
            }
        }

        Text(
            text = "⋮",
            fontSize = 20.sp,
            color = darkGreen,
            modifier = Modifier
                .clickable(onClick = onMoreClick)
                .padding(8.dp)
        )

        Text(
            text = "›",
            fontSize = 22.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun SummaryCard(
    title: String,
    value: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                vertical = 10.dp,
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.Gray
            )

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 13.sp,
                color = darkGreen
            )
        }
    }
}


@Composable
fun CollectionSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onAddPlantClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "⌕",
                fontSize = 20.sp,
                color = darkGreen
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {

                if (query.isEmpty()) {
                    Text(
                        text = "Buscar por nombre, apodo...",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = darkGreen
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "☷",
                fontSize = 20.sp,
                color = darkGreen
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(darkGreen)
                .clickable(onClick = onAddPlantClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}


@Composable
fun CollectionSummary(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onAddPlantClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Text(
            text = "♣ TU OASIS VERDE",
            fontSize = 10.sp,
            color = darkGreen
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mi Selva\nUrbana",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 25.sp,
                color = darkGreen
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "12",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Text(
                    text = "plantas",
                    fontSize = 10.sp,
                    color = darkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            SummaryCard(
                title = "Hoy",
                value = "2 por\nregar",
                icon = R.drawable.ic_water,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Clima",
                value = "22°C\nÓptimo",
                icon = R.drawable.ic_sun,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Salud",
                value = "98%\nFeliz",
                icon = R.drawable.ic_leaf,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CollectionSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onFilterClick = onFilterClick,
            onAddPlantClick = onAddPlantClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "MOSTRANDO 6 REGISTRADAS",
            fontSize = 9.sp,
            color = darkGreen,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun PlantList(
    plants: List<Plant>,
    onPlantClick: (Plant) -> Unit,
    onPlantMoreClick: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 16.dp
        )
    ) {
        items(plants) { plant ->
            PlantCard(
                plant = plant,
                onClick = {
                    onPlantClick(plant)
                },
                onMoreClick = {
                    onPlantMoreClick(plant)
                }
            )
        }
    }
}


@Composable
fun CollectionScreen(
    onFilterClick: () -> Unit = {},
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (Plant) -> Unit = {},
    onPlantMoreClick: (Plant) -> Unit = {}
) {
    val backgroundColor = Color(0xFFEAF9F0)

    var query by remember {
        mutableStateOf("")
    }

    val filteredPlants = remember(query) {
        if (query.isBlank()) {
            plants
        } else {
            plants.filter { plant ->
                plant.name.contains(
                    other = query,
                    ignoreCase = true
                )
            }
        }
    }

    FullAppScaffold(
        section = "Colección"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {

            CollectionSummary(
                query = query,
                onQueryChange = {
                    query = it
                },
                onFilterClick = onFilterClick,
                onAddPlantClick = onAddPlantClick
            )

            PlantList(
                plants = filteredPlants,
                onPlantClick = onPlantClick,
                onPlantMoreClick = onPlantMoreClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CollectionScreenPreview() {
    CollectionScreen()
}