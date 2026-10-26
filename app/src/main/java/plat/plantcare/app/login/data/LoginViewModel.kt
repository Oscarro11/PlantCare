package plat.plantcare.app.login.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface LoginEvent{
    data class ChangeUser( val user: String ): LoginEvent
    data class ChangePassword( val password: String ): LoginEvent
    data object ToggleRememberMe: LoginEvent
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onChange(event: LoginEvent){
        when (event) {
            is LoginEvent.ChangeUser -> changeUser(user = event.user)
            is LoginEvent.ChangePassword -> changePassword(password = event.password)
            is LoginEvent.ToggleRememberMe -> toggleRememberMe()
        }
    }

    private fun changeUser(user: String){
        _state.update {
            it.copy(
                user = user
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
}