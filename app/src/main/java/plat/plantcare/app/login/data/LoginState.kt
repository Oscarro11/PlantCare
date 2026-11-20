package plat.plantcare.app.login.data

data class LoginState(
    val registrationOpen: Boolean = false,
    val name: String = "",
    val location: String = "",
    val confirmation: String = "",
    val fieldErrors: AccountErrors = AccountErrors(),
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val errorMessage: String? = null
)