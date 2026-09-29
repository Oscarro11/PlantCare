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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.core.EmptyAppScaffold
import plat.plantcare.app.ui.theme.PlantcareTheme


enum class LightExposure {
    DIRECT,
    INDIRECT_HIGH,
    MEDIUM,
    LOW
}

enum class PotSize {
    SMALL,
    MEDIUM,
    LARGE
}


@Composable
private fun StepTwoHeader() {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFDFF3E6),
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
                color = Color.Gray
            )

            Text(
                text = "66% completado",
                fontSize = 9.sp,
                color = Color.Gray
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
                text = "🌱 Monstera Monstrita",
                fontSize = 8.sp,
                color = Color(0xFF52755C)
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
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = modifier
            .height(100.dp)
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    Color(0xFFE4F5EA)
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
                Color.White
            } else {
                Color(0xFF52755C)
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) {
                Color.White
            } else {
                darkGreen
            }
        )

        Text(
            text = description,
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
private fun LightExposureSection(
    selectedLight: LightExposure,
    onLightSelected: (LightExposure) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "☀ Exposición a la luz",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Text(
                text = "Elige 1 opción",
                fontSize = 9.sp,
                color = Color.Gray
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
                selected = selectedLight == LightExposure.DIRECT,
                onClick = {
                    onLightSelected(LightExposure.DIRECT)
                },
                modifier = Modifier.weight(1f)
            )

            LightOptionCard(
                title = "Indirecta alta",
                description = "Tamizada / Ventanal",
                icon = R.drawable.ic_sun,
                selected = selectedLight == LightExposure.INDIRECT_HIGH,
                onClick = {
                    onLightSelected(LightExposure.INDIRECT_HIGH)
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
                selected = selectedLight == LightExposure.MEDIUM,
                onClick = {
                    onLightSelected(LightExposure.MEDIUM)
                },
                modifier = Modifier.weight(1f)
            )

            LightOptionCard(
                title = "Baja luz",
                description = "Rincones o semisombra",
                icon = R.drawable.ic_moon,
                selected = selectedLight == LightExposure.LOW,
                onClick = {
                    onLightSelected(LightExposure.LOW)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun PotTypeSection(
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

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
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pot3),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF52755C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Terracota transpirable (con plato)",
                fontSize = 10.sp,
                color = darkGreen,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "⌄",
                fontSize = 15.sp,
                color = Color.Gray
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
    val darkGreen = Color(0xFF123D2B)

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
                Color.White
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
    val darkGreen = Color(0xFF123D2B)

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
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFDDF2E5),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(3.dp)
        ) {
            PotSizeOption(
                text = "Pequeño (S)",
                selected = selectedSize == PotSize.SMALL,
                onClick = {
                    onSizeSelected(PotSize.SMALL)
                },
                modifier = Modifier.weight(1f)
            )

            PotSizeOption(
                text = "Mediano (M)",
                selected = selectedSize == PotSize.MEDIUM,
                onClick = {
                    onSizeSelected(PotSize.MEDIUM)
                },
                modifier = Modifier.weight(1f)
            )

            PotSizeOption(
                text = "Grande (L)",
                selected = selectedSize == PotSize.LARGE,
                onClick = {
                    onSizeSelected(PotSize.LARGE)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFDFF3E6),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF52755C)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Recomendado: 18–22 cm de diámetro para dar " +
                        "espacio a raíces secundarias.",
                fontSize = 8.sp,
                lineHeight = 10.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
private fun SubstrateSection(
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

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
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(onClick = onEditClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_substratum),
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = Color(0xFF52755C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Sustrato tropical aireado (corteza + perlita)",
                fontSize = 9.sp,
                color = darkGreen,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "×",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable(onClick = onRemoveClick)
                    .padding(4.dp)
            )
        }
    }
}


@Composable
private fun StepTwoNavigation(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFDDF2E5),
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
                color = Color.White
            )
        }
    }
}


@Composable
fun RegisterPlantStep2Screen(
    onCloseClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onPotTypeClick: () -> Unit = {},
    onSubstrateClick: () -> Unit = {},
    onRemoveSubstrateClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFEAF9F0)

    var selectedLight by remember {
        mutableStateOf(LightExposure.INDIRECT_HIGH)
    }

    var selectedSize by remember {
        mutableStateOf(PotSize.MEDIUM)
    }

    EmptyAppScaffold(
        title = "Registro Planta Paso 2",
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
            StepTwoHeader()

            Spacer(modifier = Modifier.height(16.dp))

            LightExposureSection(
                selectedLight = selectedLight,
                onLightSelected = {
                    selectedLight = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PotTypeSection(
                onClick = onPotTypeClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            PotSizeSection(
                selectedSize = selectedSize,
                onSizeSelected = {
                    selectedSize = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubstrateSection(
                onEditClick = onSubstrateClick,
                onRemoveClick = onRemoveSubstrateClick
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


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RegisterPlantStep2ScreenPreview() {
    PlantcareTheme {
        RegisterPlantStep2Screen()
    }
}