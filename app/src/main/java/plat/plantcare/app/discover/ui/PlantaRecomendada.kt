package plat.plantcare.app

import android.R.attr.contentDescription
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.discover.data.PlantaDetalle
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.AppTopBar
import plat.plantcare.app.discover.data.CaracteristicaPlanta
import plat.plantcare.app.discover.data.FichaBotanicaEvent
import plat.plantcare.app.discover.data.FichaBotanicaState
import plat.plantcare.app.discover.data.FichaBotanicaViewModel
import plat.plantcare.app.discover.data.PasoCuidado
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics


@Serializable
data class FichaBotanicaDestination(
    val plantaId: String
)

fun NavController.navigateToFichaBotanicaDestination(
    plantaId: String,
    navOptions: NavOptions? = null
) {
    navigate(
        FichaBotanicaDestination(plantaId),
        navOptions
    )
}

@Composable
fun FichaBotanicaRoute(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FichaBotanicaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FichaBotanicaScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
        onShareClick = onShareClick,
        modifier = modifier
    )
}

@Composable
private fun FichaBotanicaScreen(
    state: FichaBotanicaState,
    onEvent: (FichaBotanicaEvent) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Ficha botánica",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(
                                FichaBotanicaEvent.ToggleListaDeseos
                            )
                        }
                    ) {
                        Text(
                            text = if (state.enListaDeseos) "♥" else "♡",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.semantics {
                                contentDescription =
                                    if (state.enListaDeseos) {
                                        "Quitar de lista de deseos"
                                    } else {
                                        "Añadir a lista de deseos"
                                    }
                            }
                        )
                    }

                    IconButton(onClick = onShareClick) {
                        Text(
                            text = "↗",
                            fontSize = 24.sp,
                            modifier = Modifier.semantics {
                                contentDescription = "Compartir planta"
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 14.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PlantaHeroCard(
                    planta = state.planta
                )
            }

            item {
                CompatibilidadCard(
                    porcentaje = state.planta.compatibilidad
                )
            }

            item {
                PerfilPlanta(
                    descripcion = state.planta.descripcion
                )
            }

            item {
                CaracteristicasGrid(
                    caracteristicas = state.caracteristicas
                )
            }

            item {
                GuiaCuidadosCard(
                    pasos = state.pasos
                )
            }

            item {
                Button(
                    onClick = {
                        onEvent(
                            FichaBotanicaEvent.ToggleListaDeseos
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (state.enListaDeseos) {
                            "♥  Añadida a lista de deseos"
                        } else {
                            "♡  Añadir a lista de deseos"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Ficha botánica",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun FichaBotanicaScreenPreview() {
    MaterialTheme {
        FichaBotanicaScreen(
            state = FichaBotanicaState(),
            onEvent = {},
            onBackClick = {},
            onShareClick = {}
        )
    }
}


@Composable
private fun PlantaHeroCard(
    planta: PlantaDetalle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) {
                val imagen = planta.imagen

                if (imagen != null) {
                    Image(
                        painter = painterResource(imagen),
                        contentDescription = planta.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    PlaceholderPlanta()
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf<Color>(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.80f)
                                )
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "◉  Match Personalizado",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Familia ${planta.familia} · " +
                                "Origen ${planta.origen}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Text(
                        text = planta.nombre,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = planta.nombreCientifico,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EtiquetaHero(
                        texto = "☻  Dificultad muy baja"
                    )

                    EtiquetaHero(
                        texto = "☀  Luz baja a media"
                    )
                }

                EtiquetaHero(
                    texto = "≋  Purificadora top"
                )
            }
        }
    }
}

@Composable
private fun PlaceholderPlanta() {
    val backgroundColor =
        MaterialTheme.colorScheme.primaryContainer

    val gridColor =
        MaterialTheme.colorScheme.onPrimaryContainer.copy(
            alpha = 0.18f
        )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawRect(
            color = backgroundColor
        )

        val separation = 24.dp.toPx()

        var x = 0f

        while (x <= size.width) {
            drawLine(
                color = gridColor,
                start = Offset(
                    x = x,
                    y = 0f
                ),
                end = Offset(
                    x = x,
                    y = size.height
                ),
                strokeWidth = 1.dp.toPx()
            )

            x += separation
        }

        var y = 0f

        while (y <= size.height) {
            drawLine(
                color = gridColor,
                start = Offset(
                    x = 0f,
                    y = y
                ),
                end = Offset(
                    x = size.width,
                    y = y
                ),
                strokeWidth = 1.dp.toPx()
            )

            y += separation
        }
    }
}

@Composable
private fun EtiquetaHero(
    texto: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CompatibilidadCard(
    porcentaje: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Compatibilidad con tu hogar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Calculado según tus 3 plantas actuales",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$porcentaje%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "afinidad",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = {
                    porcentaje / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            CompatibilidadItem(
                icono = "♧",
                texto = "Mismo ciclo hídrico: Se regará al mismo tiempo " +
                        "que tu Sansevieria \"Espada\" cada 15–18 días."
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompatibilidadItem(
                icono = "⌂",
                texto = "Aprovecha sombras: Ocupará el rincón noroeste " +
                        "del salón donde tu Monstera \"Monty\" no prospera."
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompatibilidadItem(
                icono = "♢",
                texto = "Microclima equilibrado: Sus tallos carnosos " +
                        "aportan densidad visual sin competir por la humedad."
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Curva de aprendizaje:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Apto para principiantes",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CompatibilidadItem(
    icono: String,
    texto: String
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.55f
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = icono,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PerfilPlanta(
    descripcion: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = "Perfil de la Planta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CaracteristicasGrid(
    caracteristicas: List<CaracteristicaPlanta>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        caracteristicas.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { caracteristica ->
                    CaracteristicaCard(
                        caracteristica = caracteristica,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (fila.size == 1) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CaracteristicaCard(
    caracteristica: CaracteristicaPlanta,
    modifier: Modifier = Modifier
) {
    val contentColor = if (caracteristica.advertencia) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(9.dp),
                color = if (caracteristica.advertencia) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = caracteristica.icono,
                        fontSize = 18.sp,
                        color = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = caracteristica.titulo,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Text(
                text = caracteristica.valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Text(
                text = caracteristica.descripcion,
                style = MaterialTheme.typography.labelSmall.copy(
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GuiaCuidadosCard(
    pasos: List<PasoCuidado>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "▣",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Guía Práctica de Cuidados",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${pasos.size} pasos",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            pasos.forEachIndexed { index, paso ->
                PasoCuidadoItem(
                    numero = index + 1,
                    paso = paso
                )

                if (index < pasos.lastIndex) {
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun PasoCuidadoItem(
    numero: Int,
    paso: PasoCuidado
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = paso.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = paso.descripcion,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}