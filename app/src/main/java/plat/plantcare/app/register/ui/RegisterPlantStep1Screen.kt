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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import plat.plantcare.app.R
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.core.ui.LocalDatePickerDialog
import plat.plantcare.app.core.ui.PhotoCameraButton
import plat.plantcare.app.core.ui.PhotoGalleryButton
import plat.plantcare.app.register.data.RegisterPlantDraft
import plat.plantcare.app.register.data.RegisterPlantEvent
import plat.plantcare.app.register.data.RegisterPlantState
import plat.plantcare.app.register.data.RegisterPlantViewModel
import plat.plantcare.app.ui.theme.PlantcareTheme
import java.time.LocalDate

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
fun RegisterPlantStep1Route(
    viewModel: RegisterPlantViewModel,
    onContinue: () -> Unit,
    onClose: () -> Unit,
    onDiscard: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterPlantStep1Screen(
        state = state,
        onEvent = viewModel::handleEvent,
        onContinueClick = onContinue,
        onCloseClick = onClose,
        onDiscardClick = {
            viewModel.handleEvent(RegisterPlantEvent.Discard)
            onDiscard()
        }
    )
}

@Composable
fun RegisterPlantStep1Screen(
    onCloseClick: () -> Unit,
    onContinueClick: () -> Unit,
    onDiscardClick: () -> Unit,
    state: RegisterPlantState = RegisterPlantState(),
    onEvent: (RegisterPlantEvent) -> Unit = {}
) {
    val draft = state.draft

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registro Planta Paso 1",
                onBackClick = onCloseClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding + PaddingValues(horizontal = 20.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PASO 1 DE 3",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "33% completado",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Identidad y Fotografías",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            RegisterProgressBar(
                progress = 0.33f
            )

            Spacer(modifier = Modifier.height(18.dp))

            PhotoSection(
                draft = state.draft,
                onTakePhotoClick = { imageUrl ->
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(imageUrl = imageUrl)
                        )
                    )
                },
                onGalleryClick = { imageUrl ->
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(imageUrl = imageUrl)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            IdentitySection(
                plantName = draft.plantName,
                onPlantNameChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(plantName = it)
                        )
                    )
                },
                selectedLocation = draft.location,
                onLocationSelected = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(location = it)
                        )
                    )
                },
                onNewLocationClick = {},
                arrivalDate = draft.arrivalDate,
                onArrivalDateChange = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(arrivalDate = it)
                        )
                    )
                },
                onArrivalDateDismiss = {
                    onEvent(
                        RegisterPlantEvent.ChangeDraft(
                            draft.copy(arrivalDate = LocalDate.now())
                        )
                    )
                }
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

@Composable
fun RegisterProgressBar(
    progress: Float
) {
    val darkGreen = MaterialTheme.colorScheme.primary
    val lightGreen = darkGreen.copy(alpha = 0.10f)

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
    model: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun PhotoSection(
    draft: RegisterPlantDraft,
    onTakePhotoClick: (String) -> Unit,
    onGalleryClick: (String) -> Unit
) {
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        MainPlantPhotoPlaceholder(
            model = draft.imageUrl ?: R.drawable.ic_plant.toString()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoCameraButton(
                size = 52.dp,
                photoContent = "plant",
                textSize = 11.sp,
                textColor = MaterialTheme.colorScheme.onSurface,
                tintColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                backgroundShape = RoundedCornerShape(8.dp),
                onModelChange = onTakePhotoClick
            )

            PhotoGalleryButton(
                size = 52.dp,
                textSize = 11.sp,
                textColor = MaterialTheme.colorScheme.onSurface,
                tintColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                backgroundShape = RoundedCornerShape(8.dp),
                onModelChange = onGalleryClick
            )
        }
    }
}


@Composable
private fun PlantNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = darkGreen.copy(alpha = 0.08f),
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
            tint = MaterialTheme.colorScheme.secondary
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
    val darkGreen = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .background(
                color = if (selected) {
                    darkGreen
                } else {
                    darkGreen.copy(alpha = 0.10f)
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
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )

        Text(
            text = location.name,
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
private fun LocationSelector(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    onNewLocationClick: () -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

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
                    color = darkGreen.copy(alpha = 0.14f),
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
    date: LocalDate,
    onDateClick: () -> Unit,
    onTodayClick: () -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary
    val todaySelected = date == LocalDate.now()

    Column {
        Text(
            text = "Fecha de llegada a casa",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.secondary
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
                        color = darkGreen.copy(alpha = 0.08f),
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
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = date.toString(),
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
                            darkGreen.copy(alpha = 0.10f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onTodayClick()
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
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        darkGreen
                    }
                )
            }
        }
    }
}

@Composable
private fun ArrivalDateSection(
    arrivalDate: LocalDate,
    onArrivalDateChange: (LocalDate) -> Unit,
    onArrivalDateDismiss: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    ArrivalDateSelector(
        date = arrivalDate,
        onDateClick = { showDatePicker = true },
        onTodayClick = onArrivalDateDismiss
    )

    if (showDatePicker) {
        LocalDatePickerDialog(
            initialDate = arrivalDate,
            onDateSelected = {
                onArrivalDateChange(it)
                showDatePicker = false
            },
            onDismiss = {
                onArrivalDateDismiss()
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun IdentitySection(
    plantName: String,
    onPlantNameChange: (String) -> Unit,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    onNewLocationClick: () -> Unit,
    arrivalDate: LocalDate,
    onArrivalDateChange: (LocalDate) -> Unit,
    onArrivalDateDismiss: () -> Unit,
) {
    val darkGreen = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
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
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Obligatorio",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
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
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Espacio asignado",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        LocationSelector(
            selectedLocation = selectedLocation,
            onLocationSelected = onLocationSelected,
            onNewLocationClick = onNewLocationClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ArrivalDateSection(
            arrivalDate = arrivalDate,
            onArrivalDateChange = onArrivalDateChange,
            onArrivalDateDismiss = onArrivalDateDismiss
        )
    }
}


@Composable
fun ContinueRegisterButton(
    text: String,
    onClick: () -> Unit
) {
    val darkGreen = MaterialTheme.colorScheme.primary

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
            color = MaterialTheme.colorScheme.onPrimary
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
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = "Descartar borrador",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RegisterPlantStep1ScreenPreview() {
    PlantcareTheme {
        RegisterPlantStep1Screen(
            onCloseClick = {},
            onContinueClick = {},
            onDiscardClick = {}
        )
    }
}

