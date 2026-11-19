package plat.plantcare.app.diagnosis.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import plat.plantcare.app.core.ui.PhotoCameraButton
import plat.plantcare.app.core.ui.PhotoGalleryButton

@Serializable
data class DiagnosisDestination(val plantId: String? = null)

fun NavGraphBuilder.diagnosisScreen(onBack: () -> Unit, onAddPlant: () -> Unit) {
    composable<DiagnosisDestination> {
        val vm: DiagnosisViewModel = hiltViewModel()
        val state by vm.state.collectAsStateWithLifecycle()
        DiagnosisScreen(state, vm.fixedPlantId != null, onBack, onAddPlant, vm::selectPlant,
            vm::choosePhoto, vm::analyze, vm::save, vm::reloadPlants, vm::cameraError)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiagnosisScreen(
    state: DiagnosisState,
    fixedPlant: Boolean,
    onBack: () -> Unit,
    onAddPlant: () -> Unit,
    onSelectPlant: (String?) -> Unit,
    onPhoto: (String) -> Unit,
    onAnalyze: () -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onCameraError: () -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("Diagnosticar") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver") }
        }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.loadingPlants) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            state.error?.let { message -> item {
                Text(message, color = MaterialTheme.colorScheme.error)
                if (state.selectedId == null) TextButton(onClick = onRetry) { Text("Reintentar") }
            } }
            if (state.selectedId == null) {
                item {
                    Text("¿Qué planta quieres revisar?", style = MaterialTheme.typography.headlineSmall)
                    Text("Elige una planta de tu colección para usar sus características como contexto.")
                }
                if (!state.loadingPlants && state.plants.isEmpty()) item {
                    Text("Todavía no tienes plantas en tu colección.")
                    Button(onClick = onAddPlant) { Text("Añadir planta") }
                }
                if (state.plants.isNotEmpty()) item {
                    OutlinedTextField(search, { search = it }, label = { Text("Buscar planta") },
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                }
                val filtered = state.plants.filter { it.name.contains(search.trim(), true) }
                if (state.plants.isNotEmpty() && filtered.isEmpty()) item { Text("No se encontraron plantas.") }
                items(filtered, key = { it.id }) { plant ->
                    OutlinedCard(onClick = { onSelectPlant(plant.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(plant.name, style = MaterialTheme.typography.titleLarge)
                            if (plant.location.isNotBlank()) Text(plant.location, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                val plant = state.selectedPlant
                if (plant == null && !state.loadingPlants) item {
                    Text("Esta planta ya no está disponible en tu colección.")
                    if (!fixedPlant) TextButton(onClick = { onSelectPlant(null) }) { Text("Elegir otra planta") }
                }
                if (plant != null) {
                    item {
                        Text(plant.name, style = MaterialTheme.typography.headlineSmall)
                        if (!fixedPlant && !state.busy) TextButton(onClick = { onSelectPlant(null) }) { Text("Cambiar planta") }
                    }
                    state.photo?.let { photo -> item {
                        Card(shape = RoundedCornerShape(20.dp)) {
                            AsyncImage(model = photo, contentDescription = "Foto que se analizará",
                                contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp, max = 320.dp))
                        }
                    } }
                    if (!state.busy) item {
                        Text(if (state.result == null) "Fotografía las hojas con buena luz y enfoque." else "Puedes tomar otra foto para iniciar un nuevo análisis.")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PhotoCameraButton(
                                size = 120.dp, photoContent = "diagnosis", textSize = 14.sp,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                backgroundShape = RoundedCornerShape(18.dp), tintColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onModelChange = onPhoto, onError = onCameraError
                            )
                            PhotoGalleryButton(
                                size = 120.dp, textSize = 14.sp,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                backgroundShape = RoundedCornerShape(18.dp), tintColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onModelChange = onPhoto
                            )
                        }
                    }
                    if (state.busy) item {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text(when (state.phase) {
                            DiagnosisPhase.IMPORTING -> "Preparando foto…"
                            DiagnosisPhase.ANALYZING -> "Revisando la foto y los datos registrados…"
                            DiagnosisPhase.SAVING -> "Guardando diagnóstico…"
                            else -> ""
                        })
                    }
                    if (state.result == null) item {
                        Text("Al pulsar Analizar, la foto y los datos registrados de esta planta se envían a Google para generar una orientación con IA.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = onAnalyze, enabled = state.photo != null && !state.busy,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Analizar planta") }
                    }
                    state.result?.let { diagnostic ->
                        item { DiagnosisResultContent(diagnostic) }
                        item {
                            if (state.saved) {
                                Text("Diagnóstico guardado. Puedes consultarlo en el detalle de la planta.", color = MaterialTheme.colorScheme.primary)
                                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Listo") }
                            } else {
                                Button(onClick = onSave, enabled = !state.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                    Text("Guardar diagnóstico")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
