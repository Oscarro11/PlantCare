package plat.plantcare.app.register.ui

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import plat.plantcare.app.R
import plat.plantcare.app.core.data.plant.characteristics.domain.AttentionLevel
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantLightExposure
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantWateringFrequency
import plat.plantcare.app.core.data.plant.characteristics.domain.PotSize
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.register.data.RegisterPlantEvent
import plat.plantcare.app.register.data.RegisterPlantState
import plat.plantcare.app.register.data.RegisterPlantViewModel
import plat.plantcare.app.ui.theme.PlantcareTheme
import kotlin.math.roundToInt

@Composable
fun RegisterPlantStep3Route(
    viewModel: RegisterPlantViewModel,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onDiscard: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            state.warning?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
            onSaved()
        }
    }

    RegisterPlantStep3Screen(
        state = state,
        onEvent = viewModel::handleEvent,
        onBackClick = onBack,
        onCloseClick = onClose,
        onSaveClick = {
            viewModel.handleEvent(RegisterPlantEvent.Save)
        },
        onDiscardClick = {
            viewModel.handleEvent(RegisterPlantEvent.Discard)
            onDiscard()
        }
    )
}

@Composable
fun RegisterPlantStep3Screen(
    onCloseClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {},
    state: RegisterPlantState = RegisterPlantState(),
    onEvent: (RegisterPlantEvent) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val draft = state.draft
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        if (state.error != null) {
            Toast.makeText(
                context,
                state.error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registro Planta Paso 3",
                onBackClick = onCloseClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding + PaddingValues(horizontal = 20.dp))
        ) {
            StepThreeHeader()

            Spacer(modifier = Modifier.height(18.dp))

            PlantRegisterSummary(
                plantImage = draft.imageUrl ?: "",
                plantName = draft.plantName,
                lightExposure = draft.lightExposure,
                potSize = draft.potSize
            )

            Spacer(modifier = Modifier.height(20.dp))

            WateringRoutineSection(
                selectedFrequency = draft.wateringFrequency,
                onFrequencySelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(wateringFrequency = it)
                        )
                    )
                },
                waterVolume = draft.waterVolume,
                onWaterVolumeChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(waterVolume = it)
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.height(22.dp))

            AttentionLevelSection(
                selectedLevel = draft.attentionLevel,
                onLevelSelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(attentionLevel = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            SavePlantButton(onClick = onSaveClick, isSaving = state.isSaving,
                enabled = !state.isSaving && !state.saved)

            Spacer(modifier = Modifier.height(10.dp))

            BackToStepTwoButton(
                onClick = onBackClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            DiscardDraftButton(
                onClick = onDiscardClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StepThreeHeader() {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "PASO 3 DE 3",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Rutina de Cuidados",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = primary
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        RegisterProgressBar(
            progress = 1f
        )
    }
}


@Composable
private fun PlantRegisterSummary(
    plantImage: String,
    plantName: String,
    lightExposure: PlantLightExposure,
    potSize: PotSize
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = plantImage,
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_leaf_filled),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "${lightExposure.displayName} · Maceta ${potSize.displayName.lowercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = "Completado",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun WateringHeader() {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_water_filled),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Frecuencia de riego",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primary
            )

            Text(
                text = "En base al volumen de agua de cada riego",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun WateringFrequencyOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .background(
                color = if (selected) {
                    primary
                } else {
                    MaterialTheme.colorScheme.background
                },
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primary
            }
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}


@Composable
private fun WateringFrequencySelector(
    selectedFrequency: PlantWateringFrequency,
    onFrequencySelected: (PlantWateringFrequency) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        WateringFrequencyOption(
            title = "Muy Frecuente",
            subtitle = "Menos de 1 d",
            selected = selectedFrequency == PlantWateringFrequency.MUY_FRECUENTE,
            onClick = {
                onFrequencySelected(PlantWateringFrequency.MUY_FRECUENTE)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "Frecuente",
            subtitle = "2-7 d",
            selected = selectedFrequency == PlantWateringFrequency.FRECUENTE,
            onClick = {
                onFrequencySelected(PlantWateringFrequency.FRECUENTE)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "Moderado",
            subtitle = "8-14 d",
            selected = selectedFrequency == PlantWateringFrequency.MODERADO,
            onClick = {
                onFrequencySelected(PlantWateringFrequency.MODERADO)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "Espaciado",
            subtitle = "Más de 14 d",
            selected = selectedFrequency == PlantWateringFrequency.ESPACIADO,
            onClick = {
                onFrequencySelected(PlantWateringFrequency.ESPACIADO)
            },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun WaterVolumeRow(
    waterVolume: Int
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.small
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_water_outline),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = "Volumen aprox. estimado:",
            style = MaterialTheme.typography.labelSmall,
            color = primary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$waterVolume ml",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = primary,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                )
        )
    }
}


@Composable
private fun WateringRoutineSection(
    selectedFrequency: PlantWateringFrequency,
    onFrequencySelected: (PlantWateringFrequency) -> Unit,
    waterVolume: Int,
    onWaterVolumeChange: (Int) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Rutina de Cuidados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(14.dp)
        ) {
            WateringHeader()

            Spacer(modifier = Modifier.height(14.dp))

            val sliderValue = waterVolumeToProgress(waterVolume)

            Slider(
                value = sliderValue,
                onValueChange = { progress ->
                    onWaterVolumeChange(progressToWaterVolume(progress))
                },
                valueRange = 0f..1f
            )

            Spacer(modifier = Modifier.height(8.dp))

            WateringFrequencySelector(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = onFrequencySelected
            )

            Spacer(modifier = Modifier.height(14.dp))

            WaterVolumeRow(
                waterVolume = waterVolume
            )
        }
    }
}

private fun waterVolumeToProgress(volume: Int): Float =
    (volume.coerceIn(100, 2000) - 100) / 1900f

private fun progressToWaterVolume(progress: Float): Int {
    val volume = 100f + progress.coerceIn(0f, 1f) * 1900f
    return ((volume / 50f).roundToInt() * 50)
}


@Composable
private fun AttentionOption(
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .heightIn(min = 80.dp)
            .background(
                color = if (selected) {
                    primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(20.dp),
            tint = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primary
            }
        )
    }
}


@Composable
private fun AttentionLevelSection(
    selectedLevel: AttentionLevel,
    onLevelSelected: (AttentionLevel) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Nivel de atención",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttentionOption(
                text = "Poca",
                icon = R.drawable.ic_happy,
                selected = selectedLevel == AttentionLevel.POCO,
                onClick = {
                    onLevelSelected(AttentionLevel.POCO)
                },
                modifier = Modifier.weight(1f)
            )

            AttentionOption(
                text = "Media",
                icon = R.drawable.ic_pot2,
                selected = selectedLevel == AttentionLevel.MEDIO,
                onClick = {
                    onLevelSelected(AttentionLevel.MEDIO)
                },
                modifier = Modifier.weight(1f)
            )

            AttentionOption(
                text = "Demandante",
                icon = R.drawable.ic_complex,
                selected = selectedLevel == AttentionLevel.DEMANDANTE,
                onClick = {
                    onLevelSelected(AttentionLevel.DEMANDANTE)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SavePlantButton(onClick: () -> Unit, isSaving: Boolean = false, enabled: Boolean = true) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)) {
        if (isSaving) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        }
        Text(if (isSaving) "Guardando planta…" else "Guardar y añadir a mi colección",
            style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun BackToStepTwoButton(
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "←  Volver al Paso 2",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = primary
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RegisterPlantStep3ScreenPreview() {
    PlantcareTheme {
        RegisterPlantStep3Screen()
    }
}
