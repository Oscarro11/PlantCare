package plat.plantcare.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//TODO: adapt colors and fonts for night mode
@Composable
fun LoginScreen(modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.then(
            other = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(18.dp)
                .fillMaxSize()
        )
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = darkenColor(originalColor = MaterialTheme.colorScheme.surface))
                .weight(0.108f)
        ){
            Image(
                painter = painterResource(id = R.drawable.botanika_logo),
                contentDescription = null,
                modifier = Modifier
                    .scale(0.90f)
            )
        }

        Text(
            text = "Botanika",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .weight(0.046f)
        )

        Text(
            text = "Tu compañero botánico y santuario verde personal",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(0.027f)
        )

        Card(
            colors = CardColors(
                containerColor = darkenColor(
                    originalColor = MaterialTheme.colorScheme.surface,
                    factor = 0.05f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = Color.Unspecified,
                disabledContainerColor = Color.Unspecified
            ),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .weight(0.1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = darkenColor(
                                    originalColor = MaterialTheme.colorScheme.surface,
                                    factor = 0.1f
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.potted_plant),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxSize(0.6f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.8f)
                ) {
                    Text(
                        text = "¡Bienvenido de nuevo!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    Text(
                        text = "Ingresa a tu oasis para cuidar y monitorear tus plantas hoy."
                    )
                }
            }
        }

        TextFieldWithTitle(
            title = "Usuario o correo",
            titleIcon = R.drawable.email,
            fieldValue = "ej. elena_botanica o elena@gmail.com",
            onValueChange = {},
            fieldLeadingIcon = R.drawable.arroba,
            fieldTrailingIcon = null,
            onTrailingIconClick = {},
            visualTransformation = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.096f)
        )

        TextFieldWithTitle(
            title = "Contraseña",
            titleIcon = R.drawable.lock_closed,
            fieldValue = "password",
            onValueChange = {},
            fieldLeadingIcon = R.drawable.lock_opened,
            fieldTrailingIcon = R.drawable.visibility_eye,
            onTrailingIconClick = {},
            visualTransformation = PasswordVisualTransformation(mask = '⬤'),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.096f)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.035f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {}
                )

                Text(
                    text = "Recordarme"
                )
            }

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .clickable(
                        onClick = {}
                    )
            )
        }

        Button(
            shape = RoundedCornerShape(20.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.062f)
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .weight(0.064f)
        ) {
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "o continúa con",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.google_sign_in_light),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    onClick = {}
                )
                .fillMaxWidth()
                .weight(0.059f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(0.027f)
        ) {
            Text(
                text = "¿Aun no tienes cuenta?"
            )

            Text(
                text = "Regístrate gratis",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable(
                        onClick = {}
                    )
            )
        }
    }
}