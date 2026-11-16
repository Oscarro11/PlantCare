package plat.plantcare.app.register.ui

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import plat.plantcare.app.R
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.register.data.RegisterPlantEvent
import plat.plantcare.app.register.data.RegisterPlantState
import plat.plantcare.app.register.data.RegisterPlantViewModel
import plat.plantcare.app.ui.theme.PlantcareTheme


enum class WateringFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    THREE_WEEKS
}

enum class AttentionLevel {
    EASY,
    MODERATE,
    DEMANDING
}

@Composable
fun RegisterPlantStep3Route(
    viewModel: RegisterPlantViewModel,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onDiscard: () -> Unit,
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
        onBackClick = onBack,
        onCloseClick = onClose,
        onSaveClick = {
            Log.i("Registration", "Registration button was clicked")
            viewModel.handleEvent(RegisterPlantEvent.Save)
        },
        onDiscardClick = {
            viewModel.handleEvent(RegisterPlantEvent.Discard)
            onDiscard()
        }
    )
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
private fun PlantRegisterSummary() {
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
            Icon(
                painter = painterResource(R.drawable.ic_plant),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
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
                    text = "Monstera Monstrita",
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
                text = "Indirecta alta · Maceta M",
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
                text = "Recomendada para esta temporada",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.small
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = "Cada 7\ndías",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primary
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
    selectedFrequency: WateringFrequency,
    onFrequencySelected: (WateringFrequency) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        WateringFrequencyOption(
            title = "Diario",
            subtitle = "1d",
            selected = selectedFrequency == WateringFrequency.DAILY,
            onClick = {
                onFrequencySelected(WateringFrequency.DAILY)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "Semanal",
            subtitle = "7d",
            selected = selectedFrequency == WateringFrequency.WEEKLY,
            onClick = {
                onFrequencySelected(WateringFrequency.WEEKLY)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "Quincenal",
            subtitle = "14d",
            selected = selectedFrequency == WateringFrequency.BIWEEKLY,
            onClick = {
                onFrequencySelected(WateringFrequency.BIWEEKLY)
            },
            modifier = Modifier.weight(1f)
        )

        WateringFrequencyOption(
            title = "3 sem.",
            subtitle = "21d",
            selected = selectedFrequency == WateringFrequency.THREE_WEEKS,
            onClick = {
                onFrequencySelected(WateringFrequency.THREE_WEEKS)
            },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun WaterVolumeRow() {
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
            text = "300 - 350 ml",
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
    selectedFrequency: WateringFrequency,
    onFrequencySelected: (WateringFrequency) -> Unit,
    waterAmount: Float,
    onWaterAmountChange: (Float) -> Unit
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

            Text(
                text = "Primavera / Verano",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 4.dp
                    )
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

            Slider(
                value = waterAmount,
                onValueChange = onWaterAmountChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            WateringFrequencySelector(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = onFrequencySelected
            )

            Spacer(modifier = Modifier.height(14.dp))

            WaterVolumeRow()
        }
    }
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
                text = "Fácil",
                icon = R.drawable.ic_happy,
                selected = selectedLevel == AttentionLevel.EASY,
                onClick = {
                    onLevelSelected(AttentionLevel.EASY)
                },
                modifier = Modifier.weight(1f)
            )

            AttentionOption(
                text = "Moderado",
                icon = R.drawable.ic_pot2,
                selected = selectedLevel == AttentionLevel.MODERATE,
                onClick = {
                    onLevelSelected(AttentionLevel.MODERATE)
                },
                modifier = Modifier.weight(1f)
            )

            AttentionOption(
                text = "Exigente",
                icon = R.drawable.ic_complex,
                selected = selectedLevel == AttentionLevel.DEMANDING,
                onClick = {
                    onLevelSelected(AttentionLevel.DEMANDING)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun AttributeChip(
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .background(
                color = if (selected) {
                    primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = MaterialTheme.shapes.large
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 11.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primary
            }
        )

        if (selected) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
private fun PlantAttributesSection(
    easyCare: Boolean,
    airPurifier: Boolean,
    ownCutting: Boolean,
    onEasyCareChange: (Boolean) -> Unit,
    onAirPurifierChange: (Boolean) -> Unit,
    onOwnCuttingChange: (Boolean) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = "Atributos y Etiquetas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            AttributeChip(
                text = "Fácil de cuidar",
                icon = R.drawable.ic_leaf2,
                selected = easyCare,
                onClick = {
                    onEasyCareChange(!easyCare)
                }
            )

            AttributeChip(
                text = "Purificadora de aire",
                icon = R.drawable.ic_wind2,
                selected = airPurifier,
                onClick = {
                    onAirPurifierChange(!airPurifier)
                }
            )

            AttributeChip(
                text = "Esqueje propio",
                icon = R.drawable.ic_scissors,
                selected = ownCutting,
                onClick = {
                    onOwnCuttingChange(!ownCutting)
                }
            )
        }
    }
}


@Composable
private fun SavePlantButton(
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = primary,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Guardar y Añadir a Mi Colección",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
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

    val selectedFrequency = draft.wateringFrequency
    val waterAmount = draft.waterAmount
    val selectedLevel = draft.attentionLevel
    val easyCare = draft.easyCare
    val airPurifier = draft.airPurifier
    val ownCutting = draft.ownCutting
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

            PlantRegisterSummary()

            Spacer(modifier = Modifier.height(20.dp))

            WateringRoutineSection(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(wateringFrequency = it)
                        )
                    )
                },
                waterAmount = waterAmount,
                onWaterAmountChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(waterAmount = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            AttentionLevelSection(
                selectedLevel = selectedLevel,
                onLevelSelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(attentionLevel = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlantAttributesSection(
                easyCare = easyCare,
                airPurifier = airPurifier,
                ownCutting = ownCutting,
                onEasyCareChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(easyCare = it)
                        )
                    )
                },
                onAirPurifierChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(airPurifier = it)
                        )
                    )
                },
                onOwnCuttingChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(ownCutting = it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

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
