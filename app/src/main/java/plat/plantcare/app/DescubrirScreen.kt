package plat.plantcare.app


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PlantaRecomendada(
    @DrawableRes val imagen: Int,
    val nombre: String,
    val nombreAlternativo: String,
    val caracteristica: String,
    val match: Int
)

@Preview(showBackground = true)
@Composable
fun DescubrirScreen() {
    val plantasRecomendadas = remember {
        mutableListOf(
            PlantaRecomendada(
                imagen = R.drawable.ic_launcher_background,
                nombre = "Zamioculcas Zamiifolia",
                nombreAlternativo = "ZZ Plant",
                caracteristica = "RESISTENCIA EXTREMA",
                match = 98
            ),
            PlantaRecomendada(
                imagen = R.drawable.ic_launcher_background,
                nombre = "Maranta Leuconeura",
                nombreAlternativo = "Planta de la oración",
                caracteristica = "MOVIMIENTO FOLIAR",
                match = 94
            ),
            PlantaRecomendada(
                imagen = R.drawable.ic_launcher_background,
                nombre = "Monstera Adansonii",
                nombreAlternativo = "Monkey Mask",
                caracteristica = "TREPAdORA / COLGANTE".uppercase(),
                match = 91
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 44.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderDescubrir(
                    cantidad = plantasRecomendadas.size
                )
            }

            item {
                Spacer(Modifier.height(14.dp))

                RecomendacionesHeader()

                Spacer(Modifier.height(4.dp))
            }

            items(
                items = plantasRecomendadas,
                key = { planta -> planta.nombre }
            ) { planta ->
                PlantaRecomendadaCard(
                    planta = planta
                )
            }
        }
    }
}

@Composable
private fun HeaderDescubrir(
    cantidad: Int
) {
    Column {
        Text(
            text = "✦ ALGORITMO BOTÁNICO",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Descubre y Amplía tu Selva",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 34.sp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Recomendaciones personalizadas según la luz y " +
                    "microclima medido en tu hogar.",
            style = MaterialTheme.typography.titleLarge.copy(
                lineHeight = 30.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "$cantidad recomendaciones encontradas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecomendacionesHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "● Perfectas para tu espacio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Calibrado para: Salón con luz indirecta media",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Filtros",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PlantaRecomendadaCard(
    planta: PlantaRecomendada
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp),
                verticalAlignment = Alignment.Top
            ) {
                PlantaImage(
                    planta = planta
                )

                Spacer(Modifier.width(14.dp))

                PlantaInformation(
                    planta = planta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(18.dp))

            PlantActions()
        }
    }
}

@Composable
private fun PlantaImage(
    planta: PlantaRecomendada
) {
    Box(
        modifier = Modifier.size(
            width = 145.dp,
            height = 155.dp
        )
    ) {
        Image(
            painter = painterResource(planta.imagen),
            contentDescription = "Imagen de ${planta.nombre}",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = "✦ ${planta.match}%",
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PlantaInformation(
    planta: PlantaRecomendada,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(155.dp)
    ) {
        Text(
            text = planta.nombre,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 23.sp,
                lineHeight = 27.sp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = planta.nombreAlternativo,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = planta.caracteristica,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PlantActions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalButton(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(45.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                contentColor =
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.BookmarkAdd,
                contentDescription = null
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "+ A deseos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(45.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    MaterialTheme.colorScheme.primary,
                contentColor =
                    MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Ver cuidados",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}