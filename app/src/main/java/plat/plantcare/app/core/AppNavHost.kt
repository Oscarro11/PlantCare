package plat.plantcare.app.core

import android.net.http.SslCertificate.saveState
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import plat.plantcare.app.R
import plat.plantcare.app.login.ui.LoginDestination
import plat.plantcare.app.login.ui.loginScreen

interface TopLevelRoute

//TODO: add routes of TopLevelDestinations
enum class TopLevelDestination(
    val route: TopLevelRoute?,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    HOME(
        route = null,
        icon = R.drawable.leaf,
        label = R.string.navigation_start
    ),

    COLLECTION(
        route = null,
        icon = R.drawable.potted_plant,
        label = R.string.navigation_collection
    ),

    CALENDAR(
        route = null,
        icon = R.drawable.calendar,
        label = R.string.navigation_calendar
    ),

    DISCOVER(
        route = null,
        icon = R.drawable.explore,
        label = R.string.navigation_discover
    ),

    FAVORITES(
        route = null,
        icon = R.drawable.favorites,
        label = R.string.navigation_favorites
    )
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    /*
    val selectedDestination = TopLevelDestination.entries.firstOrNull() { destination ->
        currentDestination?.hierarchy?.any { it.hasRoute(destination.route!!::class) } == true
    }*/

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        /*
        bottomBar = {
            if (selectedDestination != null) {
                AppBottomBar(
                    selectedDestination = selectedDestination,
                    onDestinationClick = { destination ->
                        navController.navigate(
                            destination.route!!
                        )
                    }
                )
            }
        }*/
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ){
            //TODO: add graphs and screens of navigation
            loginScreen(
                onLogin = {},
                onRegister = {},
                onLoginWithGoogle = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title) },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions
    )
}

@Composable
fun AppBottomBar(
    selectedDestination: TopLevelDestination,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val selected = destination == selectedDestination
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination) },
                icon = { Icon(painterResource(destination.icon), contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}