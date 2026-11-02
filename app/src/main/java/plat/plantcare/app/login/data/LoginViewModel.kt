package plat.plantcare.app.login.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.repositories.AuthRepository
import javax.inject.Inject

sealed interface LoginEvent{
    data class ChangeUser( val user: String ): LoginEvent
    data class ChangePassword( val password: String ): LoginEvent
    data object ToggleRememberMe: LoginEvent
    data object SignIn: LoginEvent
    data object CreateAccount: LoginEvent
    data class GoogleSignIn( val token: String ): LoginEvent
    data class GoogleSignInFailed( val error: Throwable ): LoginEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onChange(event: LoginEvent){
        when (event) {
            is LoginEvent.ChangeUser -> changeUser(user = event.user)
            is LoginEvent.ChangePassword -> changePassword(password = event.password)
            is LoginEvent.ToggleRememberMe -> toggleRememberMe()
            is LoginEvent.SignIn -> signIn()
            is LoginEvent.CreateAccount -> createAccount()
            is LoginEvent.GoogleSignIn -> googleSignIn(token = event.token)
            is LoginEvent.GoogleSignInFailed -> googleSignInFail(error = event.error)
        }
    }

    private fun changeUser(user: String){
        _state.update {
            it.copy(
                email = user
            )
        }
    }

    private fun changePassword(password: String){
        _state.update {
            it.copy(
                password = password
            )
        }
    }

    private fun toggleRememberMe(){
        _state.update {
            it.copy(
                rememberMe = !_state.value.rememberMe
            )
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signInWithEmail(_state.value.email, _state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSignedIn = true) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, errorMessage = "No se pudo autenticar") }
                }
        }
    }

    private fun createAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.registerWithEmail(_state.value.email, _state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSignedIn = true) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, errorMessage = "Hubo problemas con la creación de la cuenta") }
                }
        }
    }

    private fun googleSignIn(token: String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signInWithGoogle(token)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSignedIn = true) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, errorMessage = "No se pudo iniciar sesión") }
                }
        }
    }

    private fun googleSignInFail(error: Throwable) {
        _state.update { it.copy(errorMessage = error.toString()) }
    }
}