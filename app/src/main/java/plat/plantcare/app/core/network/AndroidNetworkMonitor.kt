package plat.plantcare.app.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {

    private val manager get() = context.getSystemService(ConnectivityManager::class.java)

    override fun isOnline(): Boolean {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}