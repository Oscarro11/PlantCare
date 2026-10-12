package plat.plantcare.app.login.data

data class LoginState(
    val user: String = "",
    val password: String = "",
    val rememberMe: Boolean = false
)