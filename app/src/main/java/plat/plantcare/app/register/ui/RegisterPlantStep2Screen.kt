package plat.plantcare.app.register.ui

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import plat.plantcare.app.R
import plat.plantcare.app.core.data.plant.characteristics.domain.Climate
import plat.plantcare.app.core.data.plant.characteristics.domain.Humidity
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantLightExposure
import plat.plantcare.app.core.data.plant.characteristics.domain.PotSize
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.core.ui.CompactEnumSelector
import plat.plantcare.app.register.data.RegisterPlantEvent
import plat.plantcare.app.register.data.RegisterPlantState
import plat.plantcare.app.register.data.RegisterPlantViewModel
import plat.plantcare.app.ui.theme.PlantcareTheme

@Composable
fun RegisterPlantStep2Route(
    viewModel: RegisterPlantViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onDiscard: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterPlantStep2Screen(
        state = state,
        onEvent = viewModel::handleEvent,
        onContinueClick = onContinue,
        onBackClick = onBack,
        onCloseClick = onClose,
        onDiscardClick = {
            viewModel.handleEvent(RegisterPlantEvent.Discard)
            onDiscard()
        }
    )
}


@Composable
fun RegisterPlantStep2Screen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onContinueClick: () -> Unit,
    onDiscardClick: () -> Unit,
    state: RegisterPlantState = RegisterPlantState(),
    onEvent: (RegisterPlantEvent) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val draft = state.draft

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registro Planta Paso 2",
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
            StepTwoHeader(plantName = draft.plantName)

            Spacer(modifier = Modifier.height(16.dp))

            ClimateSection(
                climate = draft.currentClimate,
                onClimateChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(currentClimate = it)
                        )
                    )
                },
                humidity = draft.humidity,
                onHumidityChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(humidity = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LightExposureSection(
                selectedLight = draft.lightExposure,
                onLightSelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(lightExposure = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PotTypeSection(
                value = draft.potType,
                onValueChange = { type ->
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(potType = type)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PotSizeSection(
                selectedSize = draft.potSize,
                onSizeSelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(potSize = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubstrateSection(
                value = draft.substrate,
                onValueChange = { substrate ->
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(substrate = substrate)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            StepTwoNavigation(
                onBackClick = onBackClick,
                onContinueClick = onContinueClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            DiscardDraftButton(
                onClick = onDiscardClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StepTwoHeader(
    plantName: String
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium
            )
            .padding(14.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "PASO 2 DE 3",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "66% completado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        RegisterProgressBar(
            progress = 0.66f
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Requerimientos\nAmbientales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primary
            )

            Text(
                text = "🌱 $plantName",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun LightOptionCard(
    title: String,
    description: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .heightIn(min = 120.dp)
            .background(
                color = if (selected) {
                    primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerLow
                },
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primary
            }
        )

        Text(
            text = description,
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
private fun LightExposureSection(
    selectedLight: PlantLightExposure,
    onLightSelected: (PlantLightExposure) -> Unit
) {
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
                text = "☀ Exposición a la luz",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = primary
            )

            Text(
                text = "Elige 1 opción",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LightOptionCard(
                title = "Directa",
                description = "+6h de sol puro",
                icon = R.drawable.ic_filled_sun,
                selected = selectedLight == PlantLightExposure.BRILLANTE_DIRECTA,
                onClick = {
                    onLightSelected(PlantLightExposure.BRILLANTE_DIRECTA)
                },
                modifier = Modifier.weight(1f)
            )

            LightOptionCard(
                title = "Indirecta alta",
                description = "Tamizada / Ventanal",
                icon = R.drawable.ic_sun,
                selected = selectedLight == PlantLightExposure.BRILLANTE_INDIRECTA,
                onClick = {
                    onLightSelected(PlantLightExposure.BRILLANTE_INDIRECTA)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LightOptionCard(
                title = "Luz media",
                description = "Interior iluminado",
                icon = R.drawable.ic_sunset,
                selected = selectedLight == PlantLightExposure.MEDIA,
                onClick = {
                    onLightSelected(PlantLightExposure.MEDIA)
                },
                modifier = Modifier.weight(1f)
            )

            LightOptionCard(
                title = "Baja luz",
                description = "Rincones o semisombra",
                icon = R.drawable.ic_moon,
                selected = selectedLight == PlantLightExposure.BAJA,
                onClick = {
                    onLightSelected(PlantLightExposure.BAJA)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun PotTypeSection(
    value: String,
    onValueChange: (String) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Tipo de maceta",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = primary
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pot3),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun PotSizeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .background(
                color = if (selected) {
                    primary
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.large
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primary
            }
        )
    }
}


@Composable
private fun PotSizeSection(
    selectedSize: PotSize,
    onSizeSelected: (PotSize) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Tamaño de maceta",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = primary
            )

            Text(
                text = "Diámetro estimado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large
                )
                .padding(3.dp)
        ) {
            PotSizeOption(
                text = "Pequeño (S)",
                selected = selectedSize == PotSize.PEQUENA,
                onClick = {
                    onSizeSelected(PotSize.PEQUENA)
                },
                modifier = Modifier.weight(1f)
            )

            PotSizeOption(
                text = "Mediano (M)",
                selected = selectedSize == PotSize.MEDIANA,
                onClick = {
                    onSizeSelected(PotSize.MEDIANA)
                },
                modifier = Modifier.weight(1f)
            )

            PotSizeOption(
                text = "Grande (L)",
                selected = selectedSize == PotSize.GRANDE,
                onClick = {
                    onSizeSelected(PotSize.GRANDE)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun SubstrateSection(
    value: String,
    onValueChange: (String) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Mezcla de sustrato",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = primary
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_substratum),
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ClimateSection(
    climate: Climate,
    onClimateChange: (Climate) -> Unit,
    humidity: Humidity,
    onHumidityChange: (Humidity) -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_temperature),
                contentDescription = null
            )

            Text(
                text = "  Clima y humedad",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )
        }

        Spacer(Modifier.width(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactEnumSelector(
                title = "Clima",
                selectedValue = climate,
                onValueChange = onClimateChange,
                modifier = Modifier.weight(1f)
            )

            CompactEnumSelector(
                title = "Humedad",
                selectedValue = humidity,
                onValueChange = onHumidityChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun StepTwoNavigation(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClick = onBackClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "← Atrás",
                style = MaterialTheme.typography.labelMedium,
                color = primary
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = primary,
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClick = onContinueClick)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar a Cuidados\n(Paso 3)      →",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RegisterPlantStep2ScreenPreview() {
    PlantcareTheme {
        RegisterPlantStep2Screen(
            onBackClick = {},
            onContinueClick = {},
            onCloseClick = {},
            onDiscardClick = {}
        )
    }
}
