package plat.plantcare.app

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    modifier: Modifier = Modifier
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
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground
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
            visualTransformation = visualTransformation ?: VisualTransformation.None,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun IconWithBackground(
    icon: Int,
    size: Dp,
    backgroundColor: Color,
    backgroundShape: Shape,
    tintColor: Color?,
    modifier: Modifier = Modifier,
    iconScale: Float = 0.5f
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = backgroundColor,
                    shape = backgroundShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = tintColor ?: Color.Unspecified,
                modifier = Modifier
                    .fillMaxSize(iconScale)
            )
        }
    }
}