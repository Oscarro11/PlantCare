package plat.plantcare.app.login.data

data class AccountErrors(val name: String? = null, val email: String? = null,
    val password: String? = null, val confirmation: String? = null, val location: String? = null) {
    val isValid get() = listOf(name, email, password, confirmation, location).all { it == null }
}
fun validAccountEmail(email: String): Boolean = email.trim().let {
    it.length <= 254 && Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(it)
}
fun validateAccount(name: String, email: String, password: String, confirmation: String, location: String) = AccountErrors(
    name = if (name.trim().length !in 2..60) "Escribe un nombre de 2 a 60 caracteres." else null,
    email = if (!validAccountEmail(email)) "Escribe un correo válido." else null,
    password = if (password.length !in 8..128 || password.isBlank()) "Usa una contraseña de 8 a 128 caracteres." else null,
    confirmation = if (password != confirmation) "Las contraseñas no coinciden." else null,
    location = if (location.trim().length > 120) "Usa un máximo de 120 caracteres." else null
)
