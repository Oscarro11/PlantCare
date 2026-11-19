package plat.plantcare.app.login.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import plat.plantcare.app.login.data.*

@Composable
internal fun RegisterAccountDialog(state: LoginState, onEvent: (LoginEvent) -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    AlertDialog(onDismissRequest = { if (!state.isLoading) onEvent(LoginEvent.CloseRegistration) },
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text("Crear tu cuenta") },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Completa tus datos para crear tu jardín.")
                AccountField("Nombre",state.name,state.fieldErrors.name,state.isLoading, { onEvent(LoginEvent.ChangeName(it)) })
                AccountField("Correo",state.email,state.fieldErrors.email,state.isLoading, { onEvent(LoginEvent.ChangeUser(it)) },KeyboardType.Email)
                AccountField("Ciudad y país (opcional)",state.location,state.fieldErrors.location,state.isLoading, { onEvent(LoginEvent.ChangeLocation(it)) })
                AccountField("Contraseña",state.password,state.fieldErrors.password,state.isLoading, { onEvent(LoginEvent.ChangePassword(it)) },KeyboardType.Password,!visible)
                Text("De 8 a 128 caracteres.", style = MaterialTheme.typography.bodySmall)
                AccountField("Confirmar contraseña",state.confirmation,state.fieldErrors.confirmation,state.isLoading, { onEvent(LoginEvent.ChangeConfirmation(it)) },KeyboardType.Password,!visible)
                TextButton(enabled = !state.isLoading, onClick = { visible = !visible }) { Text(if (visible) "Ocultar contraseñas" else "Mostrar contraseñas") }
                state.errorMessage?.let { Text(it,color = MaterialTheme.colorScheme.error) }
                if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(enabled = !state.isLoading,onClick = { onEvent(LoginEvent.CreateAccount) }) { Text(if (state.isLoading) "Creando…" else "Crear cuenta") } },
        dismissButton = { TextButton(enabled = !state.isLoading,onClick = { onEvent(LoginEvent.CloseRegistration) }) { Text("Cancelar") } }
    )
}
@Composable
private fun AccountField(label: String,value: String,error: String?,busy: Boolean,onValue: (String)->Unit,
    keyboard: KeyboardType = KeyboardType.Text, secret: Boolean = false) {
    OutlinedTextField(value = value,onValueChange = onValue,label = { Text(label) },isError = error != null,
        supportingText = error?.let { { Text(it) } }, enabled = !busy,singleLine = true,
        visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard,autoCorrectEnabled = false),modifier = Modifier.fillMaxWidth())
}
