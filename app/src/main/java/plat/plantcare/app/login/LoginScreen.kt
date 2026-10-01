package plat.plantcare.app.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import plat.plantcare.app.R
import plat.plantcare.app.core.IconWithBackground
import plat.plantcare.app.core.TextFieldWithTitle
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.core.relativePadding


@Preview(
    name = "Preview Small Phone",
    showBackground = true,
    widthDp = 360,
    heightDp = 640)
@Preview(
    name = "Preview Medium Phone",
    showBackground = true,
    widthDp = 412,
    heightDp = 915)
@Preview(
    name = "Preview Large Phone",
    showBackground = true,
    widthDp = 448,
    heightDp = 998)
@Composable
fun LoginScreenPreview(){
    LoginScreen()
}

//TODO: adapt colors and fonts for night mode
@Composable
fun LoginRoute(
    modifier: Modifier = Modifier
) {
    LoginScreen(modifier)
}

@Composable
private fun LoginScreen(modifier: Modifier = Modifier){
    BoxWithConstraints(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        val generalPadding = relativePadding(
            factor = 0.06f,
            minimum = 16.dp,
            maximum = 40.dp
        )

        val cardHorizontalPadding = relativePadding(
            factor = 0.04f,
            minimum = 12.dp,
            maximum = 20.dp
        )

        val itemSpacing = when {
            maxHeight < 700.dp -> 12.dp
            maxHeight < 900.dp -> 16.dp
            else -> 20.dp
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = generalPadding,
                    vertical = itemSpacing
                )
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(color = darkenColor(originalColor = MaterialTheme.colorScheme.surface))
            ){
                Image(
                    painter = painterResource(id = R.drawable.botanika_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = "Botanika",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Tu compañero botánico y santuario verde personal",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = darkenColor(
                        originalColor = MaterialTheme.colorScheme.surface,
                        factor = 0.05f
                    )
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = cardHorizontalPadding,
                            vertical = 12.dp
                        )
                ) {
                    IconWithBackground(
                        icon = R.drawable.potted_plant,
                        size = 48.dp,
                        backgroundColor = darkenColor(
                            originalColor = MaterialTheme.colorScheme.surface,
                            factor = 0.1f
                        ),
                        backgroundShape = RoundedCornerShape(8.dp),
                        tintColor = MaterialTheme.colorScheme.primary,
                        iconScale = 0.6f
                    ) {}

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "¡Bienvenido de nuevo!",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )

                        Text(
                            text = "Ingresa a tu oasis para cuidar y monitorear tus plantas hoy.",
                            style = MaterialTheme.typography.bodyMedium
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
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 320.dp) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        RememberMeOption()
                        ForgotPasswordLink()
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RememberMeOption()
                        ForgotPasswordLink()
                    }
                }
            }

            Button(
                shape = RoundedCornerShape(20.dp),
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    painter = painterResource(id = R.drawable.arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "o continúa con",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.google_sign_in_light),
                contentDescription = "Iniciar sesión con Google",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(40.dp)
                    .clickable(
                        onClick = {}
                    )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Aun no tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Regístrate gratis",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable(
                        onClick = {}
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun RememberMeOption() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = false,
            onCheckedChange = {}
        )

        Text(
            text = "Recordarme",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ForgotPasswordLink() {
    Text(
        text = "¿Olvidaste tu contraseña?",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.clickable(
            onClick = {}
        )
    )
}