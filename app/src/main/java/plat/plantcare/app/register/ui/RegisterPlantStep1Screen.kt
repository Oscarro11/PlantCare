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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import plat.plantcare.app.R
import plat.plantcare.app.core.navigation.AppTopBar
import plat.plantcare.app.core.ui.PhotoCameraButton
import plat.plantcare.app.core.ui.PhotoGalleryButton
import plat.plantcare.app.register.data.RegisterPlantDraft
import plat.plantcare.app.register.data.RegisterPlantEvent
import plat.plantcare.app.register.data.RegisterPlantState
import plat.plantcare.app.register.data.RegisterPlantViewModel
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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PASO 1 DE 3",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "33% completado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Identidad y Fotografías",
                style = MaterialTheme.typography.titleLarge,
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
                onDateClick = {}
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
    val primary = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .background(
                color = trackColor,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(5.dp)
                .background(
                    color = primary,
                    shape = MaterialTheme.shapes.small
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
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.medium
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
private fun PhotoThumbnail(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.extraSmall
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_images),
            contentDescription = "Fotografía",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
private fun AddPhotoButton(
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .size(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.extraSmall
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cameraplus),
            contentDescription = "Añadir fotografía",
            modifier = Modifier.size(18.dp),
            tint = primary
        )

        Text(
            text = "Añadir",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
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
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Fotografía del espécimen",
                style = MaterialTheme.typography.titleMedium,
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
                textSize = MaterialTheme.typography.labelMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                tintColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                backgroundShape = MaterialTheme.shapes.extraSmall,
                onModelChange = onTakePhotoClick
            )

            PhotoGalleryButton(
                size = 52.dp,
                textSize = MaterialTheme.typography.labelMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                tintColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                backgroundShape = MaterialTheme.shapes.extraSmall,
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
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.extraSmall
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
            cursorBrush = SolidColor(primary),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
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
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .background(
                color = if (selected) {
                    primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                shape = MaterialTheme.shapes.large
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
private fun LocationSelector(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    onNewLocationClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

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
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.large
                )
                .clickable(onClick = onNewLocationClick)
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        ) {
            Text(
                text = "+ Nueva estancia",
                style = MaterialTheme.typography.labelSmall,
                color = primary
            )
        }
    }
}


@Composable
private fun ArrivalDateSelector(
    onDateClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    var todaySelected by remember {
        mutableStateOf(false)
    }

    Column {
        Text(
            text = "Fecha de llegada a casa",
            style = MaterialTheme.typography.labelSmall,
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
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.extraSmall
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
                    text = if (todaySelected) {
                        "Hoy"
                    } else {
                        "12 de Octubre, 2024"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = primary
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (todaySelected) {
                            primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        shape = MaterialTheme.shapes.extraSmall
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
                    style = MaterialTheme.typography.bodySmall,
                    color = if (todaySelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        primary
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
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
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
                tint = primary
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Identidad y Origen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Apodo o nombre cariñoso",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Obligatorio",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        PlantNameField(
            value = plantName,
            onValueChange = onPlantNameChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Ubicación en el hogar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Espacio asignado",
                style = MaterialTheme.typography.labelSmall,
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
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = primary,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text   →",
            style = MaterialTheme.typography.titleSmall,
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
            style = MaterialTheme.typography.labelSmall,
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

