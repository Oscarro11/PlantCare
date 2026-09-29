package plat.plantcare.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.core.EmptyAppScaffold
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
private fun StepThreeHeader() {
    val darkGreen = Color(0xFF123D2B)

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
                color = Color.Gray
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
private fun PlantRegisterSummary() {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = Color(0xFFDDF2E5),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plant),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color(0xFF52755C)
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_leaf_filled),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF52755C)
                )
            }

            Text(
                text = "Indirecta alta · Maceta M",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = "Completado",
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF9DB5A4)
        )
    }
}


@Composable
private fun WateringHeader() {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = Color(0xFFDDF2E5),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_water_filled),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = Color(0xFF52755C)
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
                text = "Recomendada para esta temporada",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFEAF9F0),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = "Cada 7\ndías",
                fontSize = 16.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
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
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color(0xFFEAF9F0)
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
                Color.White
            } else {
                darkGreen
            }
        )

        Text(
            text = subtitle,
            fontSize = 8.sp,
            color = if (selected) {
                Color.White.copy(alpha = 0.7f)
            } else {
                Color.Gray
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
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEAF9F0),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_water_outline),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF52755C)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = "Volumen aprox. estimado:",
            fontSize = 9.sp,
            color = darkGreen,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "300 - 350 ml",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = darkGreen,
            modifier = Modifier
                .background(
                    color = Color.White,
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
    selectedFrequency: WateringFrequency,
    onFrequencySelected: (WateringFrequency) -> Unit,
    waterAmount: Float,
    onWaterAmountChange: (Float) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

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

            Text(
                text = "Primavera / Verano",
                fontSize = 9.sp,
                color = Color(0xFF52755C),
                modifier = Modifier
                    .background(
                        color = Color(0xFFDDF2E5),
                        shape = RoundedCornerShape(20.dp)
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
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp)
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
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = modifier
            .height(72.dp)
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color.White
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
                Color.White
            } else {
                Color(0xFF52755C)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            fontSize = 10.sp,
            color = if (selected) {
                Color.White
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
    val darkGreen = Color(0xFF123D2B)

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
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color.White
                },
                shape = RoundedCornerShape(20.dp)
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
                Color.White
            } else {
                Color(0xFF52755C)
            }
        )

        Text(
            text = text,
            fontSize = 9.sp,
            color = if (selected) {
                Color.White
            } else {
                darkGreen
            }
        )

        if (selected) {
            Text(
                text = "✓",
                fontSize = 9.sp,
                color = Color.White
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
    val darkGreen = Color(0xFF123D2B)

    Column {
        Text(
            text = "Atributos y Etiquetas",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = darkGreen
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
    val darkGreen = Color(0xFF123D2B)

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
            color = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Guardar y Añadir a Mi Colección",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}


@Composable
private fun BackToStepTwoButton(
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFDFF3E6),
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


@Composable
fun RegisterPlantStep3Screen(
    onCloseClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFEAF9F0)

    var selectedFrequency by remember {
        mutableStateOf(WateringFrequency.WEEKLY)
    }

    var waterAmount by remember {
        mutableFloatStateOf(0.5f)
    }

    var selectedLevel by remember {
        mutableStateOf(AttentionLevel.MODERATE)
    }

    var easyCare by remember {
        mutableStateOf(true)
    }

    var airPurifier by remember {
        mutableStateOf(true)
    }

    var ownCutting by remember {
        mutableStateOf(false)
    }

    EmptyAppScaffold(
        title = "Registro Planta Paso 3",
        onBackClick = onCloseClick,
        showBottomBar = false,
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .background(
                        color = Color(0xFFB7DCC5),
                        shape = CircleShape
                    )
                    .clickable(onClick = onProfileClick)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            StepThreeHeader()

            Spacer(modifier = Modifier.height(18.dp))

            PlantRegisterSummary()

            Spacer(modifier = Modifier.height(20.dp))

            WateringRoutineSection(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = {
                    selectedFrequency = it
                },
                waterAmount = waterAmount,
                onWaterAmountChange = {
                    waterAmount = it
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            AttentionLevelSection(
                selectedLevel = selectedLevel,
                onLevelSelected = {
                    selectedLevel = it
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlantAttributesSection(
                easyCare = easyCare,
                airPurifier = airPurifier,
                ownCutting = ownCutting,
                onEasyCareChange = {
                    easyCare = it
                },
                onAirPurifierChange = {
                    airPurifier = it
                },
                onOwnCuttingChange = {
                    ownCutting = it
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