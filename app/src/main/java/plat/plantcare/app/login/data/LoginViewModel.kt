package plat.plantcare.app.login.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.authentication.remote.AuthError
import javax.inject.Inject

sealed interface LoginEvent {
    data class ChangeUser(val user: String): LoginEvent
    data class ChangePassword(val password: String): LoginEvent
    data class ChangeName(val value: String): LoginEvent
    data class ChangeLocation(val value: String): LoginEvent
    data class ChangeConfirmation(val value: String): LoginEvent
    data object OpenRegistration: LoginEvent
    data object CloseRegistration: LoginEvent
    data object ToggleRememberMe: LoginEvent
    data object SignIn: LoginEvent
    data object CreateAccount: LoginEvent
    data class GoogleSignIn(val token: String): LoginEvent
    data class GoogleSignInFailed(val error: Throwable): LoginEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    fun onChange(event: LoginEvent) {
        if (_state.value.isLoading) return
        _state.update { it.copy(errorMessage = null, fieldErrors = AccountErrors()) }
        when (event) {
            is LoginEvent.ChangeUser -> _state.update { it.copy(email = event.user) }
            is LoginEvent.ChangePassword -> _state.update { it.copy(password = event.password) }
            is LoginEvent.ChangeName -> _state.update { it.copy(name = event.value) }
            is LoginEvent.ChangeLocation -> _state.update { it.copy(location = event.value) }
            is LoginEvent.ChangeConfirmation -> _state.update { it.copy(confirmation = event.value) }
            LoginEvent.OpenRegistration -> _state.update { it.copy(registrationOpen = true, password = "", confirmation = "") }
            LoginEvent.CloseRegistration -> _state.update { it.copy(registrationOpen = false, password = "", confirmation = "") }
            LoginEvent.ToggleRememberMe -> _state.update { it.copy(rememberMe = !it.rememberMe) }
            LoginEvent.SignIn -> signIn()
            LoginEvent.CreateAccount -> createAccount()
            is LoginEvent.GoogleSignIn -> authenticate { authRepository.signInWithGoogle(event.token) }
            is LoginEvent.GoogleSignInFailed -> _state.update { it.copy(errorMessage = "No se pudo iniciar sesión con Google.") }
        }
    }
    private fun signIn() {
        val form = _state.value
        if (!validAccountEmail(form.email) || form.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Escribe un correo válido y tu contraseña.") }; return
        }
        authenticate { authRepository.signInWithEmail(form.email.trim(), form.password) }
    }
    private fun createAccount() {
        val form = _state.value
        val errors = validateAccount(form.name, form.email, form.password, form.confirmation, form.location)
        if (!errors.isValid) { _state.update { it.copy(fieldErrors = errors) }; return }
        authenticate { authRepository.registerWithEmail(form.email.trim(),form.password,form.name.trim(),form.location.trim()) }
    }
    private fun authenticate(action: suspend () -> Result<Unit>) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                action().fold(
                    onSuccess = { _state.update { it.copy(isLoading = false, isSignedIn = true, password = "", confirmation = "") } },
                    onFailure = { failure -> _state.update { it.copy(isLoading = false, errorMessage = failure.userMessage()) } }
                )
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { _state.update { it.copy(isLoading = false, errorMessage = failure.userMessage()) } }
        }
    }
}
private fun Throwable.userMessage(): String = when (this) {
    is AuthError.EmailAlreadyInUse -> "Este correo ya tiene una cuenta. Inicia sesión."
    is AuthError.InvalidCredentials -> "El correo o la contraseña no son correctos."
    is AuthError.WeakPassword -> "La contraseña no cumple los requisitos de Firebase."
    is AuthError.NoNetwork -> "Sin conexión. Revisa Internet e inténtalo de nuevo."
    is AuthError.TooManyRequests -> "Demasiados intentos. Espera un momento y vuelve a intentarlo."
    is AuthError.ProfileUnavailable -> "No se pudo cargar el perfil. Revisa la conexión."
    else -> "No se pudo completar la operación. Inténtalo de nuevo."
}
