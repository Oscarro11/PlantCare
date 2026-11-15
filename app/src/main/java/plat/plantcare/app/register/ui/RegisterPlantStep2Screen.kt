package plat.plantcare.app.register.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = darkGreen.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PASO 2 DE 3",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "66% completado",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        RegisterProgressBar(
            progress = 0.66f
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Requerimientos\nAmbientales",
                fontSize = 17.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = "🌱 $plantName",
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.secondary
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .height(100.dp)
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    darkGreen.copy(alpha = 0.10f)
                },
                shape = RoundedCornerShape(12.dp)
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
                MaterialTheme.colorScheme.secondary
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                darkGreen
            }
        )

        Text(
            text = description,
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
private fun LightExposureSection(
    selectedLight: PlantLightExposure,
    onLightSelected: (PlantLightExposure) -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "☀  Exposición a la luz",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = "Elige 1 opción",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Tipo de maceta",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pot3),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                darkGreen
            }
        )
    }
}


@Composable
private fun PotSizeSection(
    selectedSize: PotSize,
    onSizeSelected: (PotSize) -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tamaño de maceta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = "Diámetro estimado",
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = darkGreen.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(20.dp)
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Mezcla de sustrato",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_substratum),
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = darkGreen.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(10.dp)
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
                fontSize = 11.sp,
                color = darkGreen
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = darkGreen,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(onClick = onContinueClick)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar a Cuidados\n(Paso 3)      →",
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 13.sp,
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
