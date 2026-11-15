package plat.plantcare.app.register.ui

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
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
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            onSaved()
        }
    }

    RegisterPlantStep3Screen(
        state = state,
        onEvent = viewModel::handleEvent,
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        onSaveClick = {
            viewModel.handleEvent(RegisterPlantEvent.Save)
        },
        onDiscardClick = {
            viewModel.handleEvent(RegisterPlantEvent.Discard)
            onDiscardClick()
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

            SavePlantButton(
                onClick = onSaveClick
            )

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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PASO 3 DE 3",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Rutina de Cuidados",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = darkGreen
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(10.dp)
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_leaf_filled),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "${lightExposure.displayName} · Maceta ${potSize.displayName.lowercase()}",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = "Completado",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun WateringHeader() {
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_water_filled),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Frecuencia de riego",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = "En base al volumen de agua de cada riego",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                },
                shape = RoundedCornerShape(9.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 9.sp,
            maxLines = 1,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                darkGreen
            }
        )

        Text(
            text = subtitle,
            fontSize = 8.sp,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.secondary
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_water_outline),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = "Volumen aprox. estimado:",
            fontSize = 9.sp,
            color = darkGreen,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$waterVolume ml",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = darkGreen,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rutina de Cuidados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .height(72.dp)
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(12.dp)
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
                MaterialTheme.colorScheme.secondary
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            fontSize = 10.sp,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                darkGreen
            }
        )
    }
}


@Composable
private fun AttentionLevelSection(
    selectedLevel: AttentionLevel,
    onLevelSelected: (AttentionLevel) -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Nivel de atención",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
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
private fun SavePlantButton(
    onClick: () -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = darkGreen,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Guardar y Añadir a Mi Colección",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
private fun BackToStepTwoButton(
    onClick: () -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "←  Volver al Paso 2",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = darkGreen
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
