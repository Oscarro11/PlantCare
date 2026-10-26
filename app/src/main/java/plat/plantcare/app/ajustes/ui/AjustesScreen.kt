package plat.plantcare.app


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import plat.plantcare.app.collection.data.CollectionLayout
import plat.plantcare.app.core.AppTopBar
import plat.plantcare.app.settings.data.AjustesEvent
import plat.plantcare.app.settings.data.AjustesState
import plat.plantcare.app.settings.data.AjustesViewModel

@Composable
fun AjustesRoute(
    onBackClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AjustesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AjustesScreen(
        state = state,
        onBackClick = onBackClick,
        onCerrarSesion = onCerrarSesion,
        onRecordatoriosChange = { enabled ->
            viewModel.onEvent(
                AjustesEvent.ChangeRecordatorios(enabled)
            )
        },
        onAlertasChange = { enabled ->
            viewModel.onEvent(
                AjustesEvent.ChangeAlertasClimaticas(enabled)
            )
        },
        onVistaColeccionChange = { layout ->
            viewModel.onEvent(
                AjustesEvent.ChangeVistaColeccion(layout)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun AjustesScreen(
    state: AjustesState,
    onBackClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    onRecordatoriosChange: (Boolean) -> Unit,
    onAlertasChange: (Boolean) -> Unit,
    onVistaColeccionChange: (CollectionLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Ajustes",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            FotoPerfil(
                fotoModel = state.fotoUrl
            )

            Spacer(Modifier.height(16.dp))

            Estadisticas(
                plantasVivas = state.plantasVivas,
                riegosHechos = state.riegosHechos,
                diasRacha = state.diasRacha
            )

            Spacer(Modifier.height(20.dp))

            SectionTitle(
                icon = Icons.Outlined.Notifications,
                title = "NOTIFICACIONES Y RECORDATORIOS"
            )

            NotificationsCard(
                recordatoriosInteligentes =
                    state.recordatoriosInteligentes,
                alertasClimaticas = state.alertasClimaticas,
                horaAviso = state.horaAviso,
                onRecordatoriosChange = onRecordatoriosChange,
                onAlertasChange = onAlertasChange
            )

            Spacer(Modifier.height(20.dp))

            SectionTitle(
                icon = Icons.Outlined.WifiTethering,
                title = "MI ENTORNO"
            )

            EnvironmentCard(
                ubicacion = state.ubicacion
            )

            Spacer(Modifier.height(20.dp))

            SectionTitle(
                icon = Icons.Outlined.Tune,
                title = "PREFERENCIAS DE LA APP"
            )

            PreferencesCard(
                vistaColeccion = state.vistaColeccion,
                onVistaColeccionChange = onVistaColeccionChange
            )

            Spacer(Modifier.height(28.dp))

            LogoutButton(
                onCerrarSesion = onCerrarSesion
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Botanika v2.4.0 (Build 342)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Hecho con cariño para tus plantas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.55f
                )
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AjustesScreenPreview() {
    MaterialTheme {
        AjustesScreen(
            state = AjustesState(),
            onBackClick = {},
            onCerrarSesion = {},
            onRecordatoriosChange = {},
            onAlertasChange = {},
            onVistaColeccionChange = {}
        )
    }
}

@Composable
private fun FotoPerfil(
    fotoModel: Any?
) {
    Card(
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (fotoModel != null) {
                AsyncImage(
                    model = fotoModel,
                    contentDescription = "Fotografía de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            CircleShape
                        )
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(140.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun Estadisticas(
    plantasVivas: Int,
    riegosHechos: Int,
    diasRacha: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = Icons.Outlined.LocalFlorist,
            value = plantasVivas.toString(),
            description = "Plantas vivas",
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Outlined.WaterDrop,
            value = riegosHechos.toString(),
            description = "Riegos hechos",
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Outlined.LocalFireDepartment,
            value = diasRacha.toString(),
            description = "Días racha",
            highlighted = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(5.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun NotificationsCard(
    recordatoriosInteligentes: Boolean,
    alertasClimaticas: Boolean,
    horaAviso: String,
    onRecordatoriosChange: (Boolean) -> Unit,
    onAlertasChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Recordatorios inteligentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Ajustados según el clima y la humedad local",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = recordatoriosInteligentes,
                    onCheckedChange = onRecordatoriosChange
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallIcon(Icons.Outlined.AccessTime)

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Hora de aviso habitual",
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "09:00 AM ›",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Alertas climáticas extremas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Avisos por ola de calor o heladas nocturnas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = alertasClimaticas,
                    onCheckedChange = onAlertasChange
                )
            }
        }
    }
}

@Composable
private fun EnvironmentCard(
    ubicacion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallIcon(Icons.Outlined.LocationOn)

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ubicación y Clima",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = ubicacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PreferencesCard(
    vistaColeccion: CollectionLayout,
    onVistaColeccionChange: (CollectionLayout) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            CollectionPreference(
                selectedLayout = vistaColeccion,
                onLayoutSelected = onVistaColeccionChange
            )
        }
    }
}


@Composable
private fun CollectionPreference(
    selectedLayout: CollectionLayout,
    onLayoutSelected: (CollectionLayout) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallIcon(Icons.Outlined.GridView)

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Vista de Colección",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Distribución visual",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(3.dp)
                .selectableGroup()
        ) {
            CollectionLayout.entries.forEach { layout ->
                val selected = selectedLayout == layout

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        androidx.compose.ui.graphics.Color.Transparent
                    }
                ) {
                    Text(
                        text = when (layout) {
                            CollectionLayout.GRID -> "▦ Cuadrícula"
                            CollectionLayout.LIST -> "▤ Lista"
                        },
                        modifier = Modifier
                            .selectable(
                                selected = selected,
                                role = Role.RadioButton,
                                onClick = {
                                    onLayoutSelected(layout)
                                }
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 6.dp
                            ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier.padding(
            start = 4.dp,
            bottom = 8.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.width(7.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SmallIcon(
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(11.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun LogoutButton(
    onCerrarSesion: () -> Unit
) {
    TextButton(
        onClick = onCerrarSesion,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Cerrar sesión",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}