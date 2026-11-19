package plat.plantcare.app.core.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import plat.plantcare.app.R
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun TextFieldWithTitle(
    title: String,
    titleIcon: Int?,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    fieldLeadingIcon: Int?,
    fieldTrailingIcon: Int?,
    onTrailingIconClick: () -> Unit,
    visualTransformation: VisualTransformation?,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
){
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (titleIcon != null){
                Icon(
                    painter = painterResource(id = titleIcon),
                    contentDescription = null
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(6.dp)
            )
        }
        TextField(
            value = fieldValue,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            leadingIcon = {
                if (fieldLeadingIcon != null) {
                    Icon(
                        painter = painterResource(id = fieldLeadingIcon),
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if (fieldTrailingIcon != null) {
                    IconButton(
                        onClick = onTrailingIconClick
                    ) {
                        Icon(
                            painter = painterResource(id = fieldTrailingIcon),
                            contentDescription = null
                        )
                    }
                }
            },
            placeholder = { Text(placeholder) },
            visualTransformation = visualTransformation ?: VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 200,
    heightDp = 200
)
@Composable
fun PreviewIconWithBackground() {
    IconWithBackground(
        icon = R.drawable.potted_plant,
        size = 48.dp,
        backgroundColor = Color.Green,
        backgroundShape = RoundedCornerShape(8.dp),
        tintColor = null,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Hello")
    }
}

@Composable
fun IconWithBackground(
    @DrawableRes icon: Int,
    size: Dp,
    backgroundColor: Color,
    backgroundShape: Shape,
    tintColor: Color?,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    iconScale: Float = 0.5f,
    content: @Composable (RowScope.() -> Unit)? = null
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .sizeIn(
                    minWidth = size,
                    minHeight = size
                )
                .background(
                    color = backgroundColor,
                    shape = backgroundShape
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = tintColor ?: Color.Unspecified,
                    modifier = Modifier.size(size * iconScale)
                )

                if (content != null){
                    Spacer(modifier = Modifier.size(8.dp))

                    content()
                }
            }
        }
    }
}

@Composable
fun AccordionSection(
    icon: Int,
    title: String,
    description: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { expanded = !expanded },
                        enabled = isEnabled
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = darkenColor(MaterialTheme.colorScheme.background),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }

            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    content = content
                )
            }
        }
    }
}

@Composable
fun PhotoGalleryButton(
    size: Dp,
    textSize: TextUnit,
    textColor: Color,
    backgroundColor: Color,
    backgroundShape: Shape,
    tintColor: Color?,
    onModelChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    iconScale: Float = 0.5f,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onModelChange(it.toString()) }
    }

    IconWithBackground(
        icon = R.drawable.ic_images,
        size = size,
        backgroundColor = backgroundColor,
        backgroundShape = backgroundShape,
        tintColor = tintColor,
        contentPadding = contentPadding,
        iconScale = iconScale,
        modifier = modifier
            .clickable {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
    ) {
        Text(
            text = "Elegir foto",
            fontSize = textSize,
            color = textColor
        )
    }
}

@Composable
fun PhotoCameraButton(
    size: Dp,
    photoContent: String,
    textSize: TextUnit,
    textColor: Color,
    backgroundColor: Color,
    backgroundShape: Shape,
    tintColor: Color?,
    onModelChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    iconScale: Float = 0.5f,
    onError: () -> Unit = {}
) {
    val context = LocalContext.current
    var pendingUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingUri
        pendingUri = null
        if (success && uri != null) {
            onModelChange(uri.toString())
        } else {
            uri?.let { context.contentResolver.delete(it, null, null) }
        }
    }

    IconWithBackground(
        icon = R.drawable.camera,
        size = size,
        backgroundColor = backgroundColor,
        backgroundShape = backgroundShape,
        tintColor = tintColor,
        contentPadding = contentPadding,
        iconScale = iconScale,
        modifier = modifier
            // Disabled while a photo is pending, so a second tap can't orphan the first file.
            .clickable(enabled = pendingUri == null) {
                val uri = createCameraImageUri(context, photoContent)
                pendingUri = uri
                try {
                    launcher.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    pendingUri = null
                    context.contentResolver.delete(uri, null, null)
                    onError()
                }
            }
    ) {
        Text(
            text = "Tomar foto",
            fontSize = textSize,
            color = textColor
        )
    }
}

private fun createCameraImageUri(context: Context, type: String): Uri {
    val dir = File(context.cacheDir, "photos").apply { mkdirs() }
    val file = File.createTempFile("${type}_", ".jpg", dir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate? = null,
    maxDate: LocalDate? = LocalDate.now(),
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.toPickerMillis(),
        selectableDates = remember(maxDate) { UpToDate(maxDate) }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(it.toLocalDateFromPicker()) }
                }
            ) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = state)
    }
}

@Composable
fun <T : Enum<T>> CompactEnumSelector(
    title: String,
    selectedValue: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(10.dp)

    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = colors.primary
        )

        Spacer(Modifier.height(4.dp))

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(colors.surface, shape)
                    .border(1.dp, colors.secondary, shape)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedValue.displayLabel(),
                    fontSize = 12.sp,
                    color = colors.onSurface
                )
                Text("⌄", fontSize = 16.sp, color = colors.secondary)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                enumValuesFor(selectedValue).forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option.displayLabel(), fontSize = 12.sp)
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private class UpToDate(private val max: LocalDate?) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        max == null || utcTimeMillis.toLocalDateFromPicker() <= max

    override fun isSelectableYear(year: Int): Boolean =
        max == null || year <= max.year
}

private fun Long.toLocalDateFromPicker(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

private fun LocalDate.toPickerMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<T>> enumValuesFor(value: T): List<T> {
    val valueClass = value.javaClass
    val enumClass = if (valueClass.isEnum) valueClass else valueClass.superclass
    val constants = enumClass.enumConstants ?: return emptyList()
    return constants.map { it as T }
}

private fun Enum<*>.displayLabel(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.titlecase() }