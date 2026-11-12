package plat.plantcare.app

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import plat.plantcare.app.discover.ui.CatalogPlantImage
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.discover.data.CaracteristicaPlanta
import plat.plantcare.app.discover.data.FichaBotanicaEvent
import plat.plantcare.app.discover.data.FichaBotanicaState
import plat.plantcare.app.discover.data.FichaBotanicaViewModel
import plat.plantcare.app.discover.data.PasoCuidado
import plat.plantcare.app.discover.data.PlantaDetalle


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

fun NavGraphBuilder.fichaBotanicaScreen(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    composable<FichaBotanicaDestination> {
        FichaBotanicaRoute(
            onBackClick = onBackClick,
            onShareClick = onShareClick
        )
    }
}

@Composable
fun FichaBotanicaRoute(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FichaBotanicaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    FichaBotanicaScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
        onShareClick = {
            val text = listOf(state.planta.nombre, state.planta.nombreCientifico, state.planta.descripcion).filter { it.isNotBlank() }.joinToString("\n\n")
            if (text.isNotBlank()) context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }, "Compartir planta"))
        },
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
                        enabled = !state.isLoading && !state.notFound && !state.isSaving,
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
            if (state.isLoading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            state.error?.let { message -> item {
                Text(message, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { onEvent(FichaBotanicaEvent.Retry) }) { Text("Reintentar") }
            } }
            if (state.notFound) item { Text("Esta planta ya no está disponible en el catálogo.") }
            if (!state.isLoading && !state.notFound && state.planta.nombre.isNotBlank()) {
            item {
                PlantaHeroCard(
                    planta = state.planta
                )
            }


            item {
                PerfilPlanta(
                    descripcion = state.planta.descripcion
                )
            }

            if (state.caracteristicas.isNotEmpty()) item {
                CaracteristicasGrid(
                    caracteristicas = state.caracteristicas
                )
            }

            if (state.pasos.isEmpty()) item { Text("Aún no hay una guía de cuidados para esta especie.") }
            else item {
                GuiaCuidadosCard(
                    pasos = state.pasos
                )
            }

            item {
                Button(
                    enabled = !state.isSaving,
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
                CatalogPlantImage(url = planta.imageUrl, description = planta.nombre, modifier = Modifier.fillMaxSize())

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
                        text = "Ficha de la especie",
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