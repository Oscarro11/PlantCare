package plat.plantcare.app.favorites.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import plat.plantcare.app.discover.ui.CatalogPlantImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import plat.plantcare.app.favorites.data.ListaDeseosEvent
import plat.plantcare.app.favorites.data.ListaDeseosState
import plat.plantcare.app.favorites.data.ListaDeseosViewModel
import plat.plantcare.app.favorites.data.PlantaDeseada

@Composable
fun ListaDeseosRoute(
    onAgregarClick: () -> Unit,
    onFiltrosClick: () -> Unit,
    onVerCuidados: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ListaDeseosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListaDeseosScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onAgregarClick = onAgregarClick,
        onFiltrosClick = { viewModel.onEvent(ListaDeseosEvent.ToggleOrden) },
        onVerCuidados = onVerCuidados,
        modifier = modifier
    )
}

@Composable
private fun ListaDeseosScreen(
    state: ListaDeseosState,
    onEvent: (ListaDeseosEvent) -> Unit,
    onAgregarClick: () -> Unit,
    onFiltrosClick: () -> Unit,
    onVerCuidados: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 40.dp,
            bottom = 40.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderDeseos(
                cantidad = state.plantasDeseadas.size
            )
        }

        item {
            SearchSection(
                busqueda = state.busqueda,
                onBusquedaChange = { texto ->
                    onEvent(
                        ListaDeseosEvent.ChangeBusqueda(texto)
                    )
                },
                onFiltrosClick = onFiltrosClick,
                onAgregarClick = onAgregarClick
            )
        }

        if (state.isLoading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        state.error?.let { message -> item {
            Text(message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { onEvent(ListaDeseosEvent.Retry) }) { Text("Reintentar") }
        } }
        item { Text(if (state.ordenInverso) "Orden: Z–A" else "Orden: A–Z", style = MaterialTheme.typography.labelMedium) }
        if (!state.isLoading && state.error == null && state.plantasFiltradas.isEmpty()) {
            item {
                Text(
                    text = if (state.plantasDeseadas.isEmpty()) {
                        "Tu lista de deseos está vacía."
                    } else {
                        "No se encontraron plantas."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(
            items = state.plantasFiltradas,
            key = { planta -> planta.id }
        ) { planta ->
            PlantaDeseadaCard(
                planta = planta,
                enabled = planta.id !in state.pendingIds,
                onEliminarClick = {
                    onEvent(
                        ListaDeseosEvent.EliminarPlanta(
                            plantaId = planta.id
                        )
                    )
                },
                onVerCuidados = { onVerCuidados(planta.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaDeseosScreenPreview() {
    MaterialTheme {
        ListaDeseosScreen(
            state = ListaDeseosState(),
            onEvent = {},
            onAgregarClick = {},
            onFiltrosClick = {}
        )
    }
}

@Composable
private fun HeaderDeseos(
    cantidad: Int
) {
    Column {
        Text(
            text = "Lista de Deseos",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 7.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = "$cantidad plantas soñadas para tu hogar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SearchSection(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onFiltrosClick: () -> Unit,
    onAgregarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier
                .weight(1f)
                .height(62.dp)
            ,
            placeholder = {
                Text("Buscar...", style = MaterialTheme.typography.bodyLarge)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor =
                    MaterialTheme.colorScheme.surface,
                unfocusedContainerColor =
                    MaterialTheme.colorScheme.surface,
                focusedBorderColor =
                    MaterialTheme.colorScheme.surface,
                unfocusedBorderColor =
                    MaterialTheme.colorScheme.surface
            )
        )

        FilledTonalIconButton(
            onClick = onFiltrosClick,
            modifier = Modifier.size(62.dp),
            shape = RoundedCornerShape(20.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Cambiar orden alfabético",
                modifier = Modifier.size(28.dp)
            )
        }

        FilledIconButton(
            onClick = onAgregarClick,
            modifier = Modifier.size(62.dp),
            shape = RoundedCornerShape(20.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Agregar",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun PlantaDeseadaCard(
    planta: PlantaDeseada,
    enabled: Boolean,
    onEliminarClick: () -> Unit,
    onVerCuidados: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CatalogPlantImage(
                    url = planta.imageUrl,
                    description = "Imagen de ${planta.nombre}",
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = planta.nombreCientifico, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = planta.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Button(
                    onClick = onVerCuidados,
                    modifier = Modifier
                        .weight(4f)
                        .height(45.dp),
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        bottomStart = 18.dp,
                        topEnd = 6.dp,
                        bottomEnd = 6.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFlorist,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Ver cuidados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalIconButton(
                    onClick = onEliminarClick,
                    enabled = enabled,
                    modifier = Modifier.size(45.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        bottomStart = 6.dp,
                        topEnd = 18.dp,
                        bottomEnd = 18.dp
                    ),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor =
                            MaterialTheme.colorScheme.secondaryContainer,
                        contentColor =
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Quitar de la lista de deseos"
                    )
                }
            }
        }
    }
}
