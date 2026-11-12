package plat.plantcare.app.authentication

import kotlinx.coroutines.flow.Flow

interface SessionProvider {
    val sessionUid: Flow<String?>
}