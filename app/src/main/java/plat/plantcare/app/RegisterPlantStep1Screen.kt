package plat.plantcare.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import plat.plantcare.app.core.EmptyAppScaffold
import plat.plantcare.app.ui.theme.PlantcareTheme


data class PlantLocation(
    val name: String,
    @DrawableRes val icon: Int
)


private val plantLocations = listOf(
    PlantLocation("Salón", R.drawable.ic_livingroom),
    PlantLocation("Dormitorio", R.drawable.ic_bedroom),
    PlantLocation("Balcón", R.drawable.ic_balcony),
    PlantLocation("Cocina", R.drawable.ic_kitchen),
    PlantLocation("Baño", R.drawable.ic_bathroom),
    PlantLocation("Estudio", R.drawable.ic_study)
)


@Composable
fun RegisterProgressBar(
    progress: Float
) {
    val darkGreen = Color(0xFF123D2B)
    val lightGreen = Color(0xFFD6EBDD)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .background(
                color = lightGreen,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(5.dp)
                .background(
                    color = darkGreen,
                    shape = RoundedCornerShape(10.dp)
                )
        )
    }
}


@Composable
private fun MainPlantPhotoPlaceholder(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                color = Color(0xFFD6EBDD),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_images),
            contentDescription = "Fotografía de la planta",
            modifier = Modifier.size(42.dp),
            tint = Color(0xFF52755C)
        )
    }
}


@Composable
private fun PhotoActionButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = modifier
            .height(46.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = darkGreen
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = text,
            fontSize = 11.sp,
            color = darkGreen
        )
    }
}


@Composable
private fun PhotoThumbnail(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(
                color = Color(0xFFD6EBDD),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_images),
            contentDescription = "Fotografía",
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF52755C)
        )
    }
}


@Composable
private fun AddPhotoButton(
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier
            .size(52.dp)
            .background(
                color = Color(0xFFDDF2E5),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cameraplus),
            contentDescription = "Añadir fotografía",
            modifier = Modifier.size(18.dp),
            tint = darkGreen
        )

        Text(
            text = "Añadir",
            fontSize = 7.sp,
            color = Color.Gray
        )
    }
}


@Composable
private fun PhotoSection(
    onTakePhotoClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onAddPhotoClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fotografía del espécimen",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = darkGreen
            )

            Text(
                text = "1 de 3 fotos",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        MainPlantPhotoPlaceholder(
            onClick = onPhotoClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoActionButton(
                text = "Tomar foto",
                icon = R.drawable.ic_camera,
                onClick = onTakePhotoClick,
                modifier = Modifier.weight(1f)
            )

            PhotoActionButton(
                text = "Desde galería",
                icon = R.drawable.ic_images,
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoThumbnail(
                onClick = onPhotoClick
            )

            PhotoThumbnail(
                onClick = onPhotoClick
            )

            AddPhotoButton(
                onClick = onAddPhotoClick
            )
        }
    }
}


@Composable
private fun PlantNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEAF9F0),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_happy),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF52755C)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 11.sp,
                color = darkGreen
            )
        )
    }
}


@Composable
private fun LocationChip(
    location: PlantLocation,
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
                    Color(0xFFDDF2E5)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(location.icon),
            contentDescription = location.name,
            modifier = Modifier.size(13.dp),
            tint = if (selected) {
                Color.White
            } else {
                Color(0xFF52755C)
            }
        )

        Text(
            text = location.name,
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
private fun LocationSelector(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    onNewLocationClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        plantLocations.forEach { location ->
            LocationChip(
                location = location,
                selected = selectedLocation == location.name,
                onClick = {
                    onLocationSelected(location.name)
                }
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFCBEBCF),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onNewLocationClick)
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        ) {
            Text(
                text = "+ Nueva estancia",
                fontSize = 9.sp,
                color = darkGreen
            )
        }
    }
}


@Composable
private fun ArrivalDateSelector(
    onDateClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    var todaySelected by remember {
        mutableStateOf(false)
    }

    Column {
        Text(
            text = "Fecha de llegada a casa",
            fontSize = 9.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color(0xFFEAF9F0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(onClick = onDateClick)
                    .padding(11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF52755C)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (todaySelected) {
                        "Hoy"
                    } else {
                        "12 de Octubre, 2024"
                    },
                    fontSize = 10.sp,
                    color = darkGreen
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (todaySelected) {
                            darkGreen
                        } else {
                            Color(0xFFDDF2E5)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        todaySelected = !todaySelected
                    }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 11.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hoy",
                    fontSize = 10.sp,
                    color = if (todaySelected) {
                        Color.White
                    } else {
                        darkGreen
                    }
                )
            }
        }
    }
}


@Composable
private fun IdentitySection(
    plantName: String,
    onPlantNameChange: (String) -> Unit,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    onNewLocationClick: () -> Unit,
    onDateClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_id),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = darkGreen
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Identidad y Origen",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Apodo o nombre cariñoso",
                fontSize = 9.sp,
                color = Color.Gray
            )

            Text(
                text = "Obligatorio",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        PlantNameField(
            value = plantName,
            onValueChange = onPlantNameChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ubicación en el hogar",
                fontSize = 9.sp,
                color = Color.Gray
            )

            Text(
                text = "Espacio asignado",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        LocationSelector(
            selectedLocation = selectedLocation,
            onLocationSelected = onLocationSelected,
            onNewLocationClick = onNewLocationClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ArrivalDateSelector(
            onDateClick = onDateClick
        )
    }
}


@Composable
fun ContinueRegisterButton(
    text: String,
    onClick: () -> Unit
) {
    val darkGreen = Color(0xFF123D2B)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = darkGreen,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text   →",
            fontSize = 14.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}


@Composable
fun DiscardDraftButton(
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_trash),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = "Descartar borrador",
            fontSize = 9.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun RegisterPlantStep1Screen(
    onCloseClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTakePhotoClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onPhotoClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onNewLocationClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFEAF9F0)
    val darkGreen = Color(0xFF123D2B)

    var plantName by remember {
        mutableStateOf("Monstera Monstrita")
    }

    var selectedLocation by remember {
        mutableStateOf("Salón")
    }

    EmptyAppScaffold(
        title = "Registro Planta Paso 1",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PASO 1 DE 3",
                    fontSize = 9.sp,
                    color = Color.Gray
                )

                Text(
                    text = "33% completado",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Identidad y Fotografías",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            RegisterProgressBar(
                progress = 0.33f
            )

            Spacer(modifier = Modifier.height(18.dp))

            PhotoSection(
                onTakePhotoClick = onTakePhotoClick,
                onGalleryClick = onGalleryClick,
                onPhotoClick = onPhotoClick,
                onAddPhotoClick = onAddPhotoClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            IdentitySection(
                plantName = plantName,
                onPlantNameChange = {
                    plantName = it
                },
                selectedLocation = selectedLocation,
                onLocationSelected = {
                    selectedLocation = it
                },
                onNewLocationClick = onNewLocationClick,
                onDateClick = onDateClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            ContinueRegisterButton(
                text = "Continuar a Requerimientos\n(Paso 2)",
                onClick = onContinueClick
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
private fun RegisterPlantStep1ScreenPreview() {
    PlantcareTheme {
        RegisterPlantStep1Screen()
    }
}