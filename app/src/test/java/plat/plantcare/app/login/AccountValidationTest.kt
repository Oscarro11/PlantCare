package plat.plantcare.app.login

import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.login.data.*

class AccountValidationTest {
    @Test fun acceptsNamesWithAccentsAndTrimsEmailWithoutChangingPassword() {
        assertTrue(validateAccount("  José Pérez  ","  daniel@example.com  ","Mi clave 2026","Mi clave 2026","Guatemala, Guatemala").isValid)
    }
    @Test fun marksEveryInvalidField() {
        val errors=validateAccount(" ","correo inválido","123","456","x".repeat(121))
        assertFalse(errors.isValid)
        assertNotNull(errors.name); assertNotNull(errors.email); assertNotNull(errors.password)
        assertNotNull(errors.confirmation); assertNotNull(errors.location)
    }
    @Test fun rejectsBlankOrExcessivePasswordsAndMismatchedConfirmation() {
        assertNotNull(validateAccount("Daniel","d@example.com","        ","        ","").password)
        assertNotNull(validateAccount("Daniel","d@example.com","x".repeat(129),"x".repeat(129),"").password)
        assertNotNull(validateAccount("Daniel","d@example.com","clave2026","clave2027","").confirmation)
    }
    @Test fun optionalLocationAndFieldBoundariesAreRespected() {
        assertTrue(validateAccount("Ab","d@example.com","12345678","12345678","").isValid)
        assertNotNull(validateAccount("x".repeat(61),"d@example.com","12345678","12345678","").name)
    }
    @Test fun rejectsWhitespaceAndMissingEmailParts() {
        listOf("", "a@", "@example.com", "a b@example.com", "a@example").forEach { assertFalse(it,validAccountEmail(it)) }
        assertTrue(validAccountEmail("daniel+plantas@example.com"))
    }
}
