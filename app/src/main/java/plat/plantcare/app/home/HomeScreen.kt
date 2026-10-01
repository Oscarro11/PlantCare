package plat.plantcare.app.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.core.FullAppScaffold
import plat.plantcare.app.core.IconWithBackground
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.core.lightenColor

enum class Status {
    URGENTE,
    PRIORITARIO,
    HOY
}

data class PlantShortCardInfo(
    val name: String,
    val image: Int,
    val description: String?,
    val actionIcon: Int?,
    val location: String = "Jardín",
    val status: Status = Status.HOY
)

private val mockCardInfo = listOf(
    PlantShortCardInfo(
        name = "Monstera Deliciosa",
        image = R.drawable.plant_img_example,
        description = "Regar · 350 mL de agua filtrada",
        actionIcon = R.drawable.water_drop,
        location = "Salón",
        status = Status.URGENTE
    ),
    PlantShortCardInfo(
        name = "Helecho Boston",
        image = R.drawable.plant_img_example,
        description = "Pulverizar hojas · Humedad foliar",
        actionIcon = R.drawable.water_drop,
        location = "Baño",
        status = Status.HOY
    ),
    PlantShortCardInfo(
        name = "Ficus Lyrata",
        image = R.drawable.plant_img_example,
        description = "Girar 90° hacia la ventana",
        actionIcon = R.drawable.water_drop,
        location = "Estudio",
        status = Status.PRIORITARIO
    )
)

@Composable
private fun Status.backgroundColor(): Color = when (this) {
    Status.URGENTE -> Color.Red
    Status.PRIORITARIO -> darkenColor(MaterialTheme.colorScheme.primary)
    Status.HOY -> MaterialTheme.colorScheme.primary
}

@Composable
private fun Status.contentColor(): Color = when (this) {
    Status.URGENTE -> Color.White
    Status.PRIORITARIO,
    Status.HOY -> MaterialTheme.colorScheme.onPrimary
}

@Composable
fun HomeRoute(modifier: Modifier = Modifier) {
    HomeScreen(modifier)
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    FullAppScaffold(section = "Inicio") {
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
                HeaderSummary()
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "¡Buenos días, Elena!",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Hoy tienes 4 plantas esperando tu toque verde",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                DailyProgressCard()
            }

            item {
                TasksHeader()
            }

            items(
                items = mockCardInfo,
                key = { it.name }
            ) { item ->
                PlantShortCard(
                    data = item,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                QuickActions()
            }
        }
    }
}

@Composable
private fun HeaderSummary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MIÉRCOLES, 24 DE MAYO",
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
                text = "12 plantas activas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun DailyProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
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
                    text = "1 de 4 completadas (25%)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { 0.25f },
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
private fun TasksHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tareas de hoy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
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
fun PlantShortCard(
    data: PlantShortCardInfo,
    modifier: Modifier = Modifier
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
            Image(
                painter = painterResource(data.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
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
                        text = data.status.name,
                        modifier = Modifier
                            .background(
                                color = data.status.backgroundColor(),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = data.status.contentColor()
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
                            text = data.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                data.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            data.actionIcon?.let {
                Spacer(Modifier.width(8.dp))

                IconWithBackground(
                    icon = it,
                    size = 40.dp,
                    backgroundColor = darkenColor(
                        originalColor = MaterialTheme.colorScheme.background,
                        factor = 0.2f
                    ),
                    backgroundShape = CircleShape,
                    tintColor = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun QuickActions() {
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
    HomeScreen()
}

@Preview(showBackground = true)
@Composable
private fun PlantShortCardPreview() {
    PlantShortCard(data = mockCardInfo.first())
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