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
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.discover.data.DescubrirEvent
import plat.plantcare.app.discover.data.DescubrirState
import plat.plantcare.app.discover.data.DescubrirViewModel
import plat.plantcare.app.discover.data.PlantaRecomendada

data class PlantaRecomendada(
    @DrawableRes val imagen: Int,
    val nombre: String,
    val nombreAlternativo: String,
    val caracteristica: String,
    val match: Int
)

@Serializable
data object DescubrirDestination

fun NavController.navigateToDescubrirDestination(
    navOptions: NavOptions? = null
) {
    navigate(DescubrirDestination, navOptions)
}

fun NavGraphBuilder.descubrirScreen(
    onVerCuidados: (PlantaRecomendada) -> Unit,
    onFiltrosClick: () -> Unit
) {
    composable<DescubrirDestination> {
        DescubrirRoute(
            onVerCuidados = onVerCuidados,
            onFiltrosClick = onFiltrosClick
        )
    }
}

@Composable
fun DescubrirRoute(
    onVerCuidados: (PlantaRecomendada) -> Unit,
    onFiltrosClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DescubrirViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DescubrirScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onVerCuidados = onVerCuidados,
        onFiltrosClick = onFiltrosClick,
        modifier = modifier
    )
}

@Composable
private fun DescubrirScreen(
    state: DescubrirState,
    onEvent: (DescubrirEvent) -> Unit,
    onVerCuidados: (PlantaRecomendada) -> Unit,
    onFiltrosClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
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
                cantidad = state.plantasRecomendadas.size
            )
        }

        item {
            Column {
                Spacer(Modifier.height(14.dp))

                RecomendacionesHeader(
                    onFiltrosClick = onFiltrosClick
                )

                Spacer(Modifier.height(4.dp))
            }
        }

        items(
            items = state.plantasRecomendadas,
            key = { planta -> planta.id }
        ) { planta ->
            PlantaRecomendadaCard(
                planta = planta,
                onToggleListaDeseos = {
                    onEvent(
                        DescubrirEvent.ToggleListaDeseos(
                            plantaId = planta.id
                        )
                    )
                },
                onVerCuidados = {
                    onVerCuidados(planta)
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun DescubrirScreenPreview() {
    MaterialTheme {
        DescubrirScreen(
            state = DescubrirState(),
            onEvent = {},
            onVerCuidados = {},
            onFiltrosClick = {}
        )
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
private fun RecomendacionesHeader(
    onFiltrosClick: () -> Unit
) {
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
            onClick = onFiltrosClick
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
    planta: PlantaRecomendada,
    onToggleListaDeseos: () -> Unit,
    onVerCuidados: () -> Unit
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

            PlantActions(
                enListaDeseos = planta.enListaDeseos,
                onToggleListaDeseos = onToggleListaDeseos,
                onVerCuidados = onVerCuidados
            )
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
private fun PlantActions(
    enListaDeseos: Boolean,
    onToggleListaDeseos: () -> Unit,
    onVerCuidados: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalButton(
            onClick = onToggleListaDeseos,
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
                text = if (enListaDeseos) {
                    "En deseos"
                } else {
                    "+ A deseos"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onVerCuidados,
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