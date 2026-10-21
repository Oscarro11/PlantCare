package plat.plantcare.app.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import plat.plantcare.app.R
import plat.plantcare.app.collection.ui.CollectionDestination
import plat.plantcare.app.collection.ui.CollectionGraph
import plat.plantcare.app.collection.ui.collectionGraph
import plat.plantcare.app.core.darkenColor
import plat.plantcare.app.home.ui.HomeDestination
import plat.plantcare.app.home.ui.HomeGraph
import plat.plantcare.app.home.ui.homeGraph
import plat.plantcare.app.home.ui.navigateToHomeGraph
import plat.plantcare.app.login.ui.LoginDestination
import plat.plantcare.app.login.ui.loginScreen


interface TopLevelRoute

//TODO: add routes of TopLevelDestinations
enum class TopLevelDestination(
    val route: TopLevelRoute,
    val startRoute: Any,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    HOME(
        route = HomeGraph,
        startRoute = HomeDestination,
        icon = R.drawable.leaf,
        label = R.string.navigation_start
    ),

    COLLECTION(
        route = CollectionGraph,
        startRoute = CollectionDestination,
        icon = R.drawable.potted_plant,
        label = R.string.navigation_collection
    ),

    /*
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
    )*/
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val selectedDestination = TopLevelDestination.entries.firstOrNull() { destination ->
        currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
    }

    val usesTopAppBar = TopLevelDestination.entries.firstOrNull() { destination ->
        currentDestination?.hierarchy?.any { it.hasRoute(destination.startRoute::class) } == true
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            if (usesTopAppBar != null) {
                AppSectionTopBar(
                    section = stringResource(usesTopAppBar.label),
                    //TODO: add account screen to navigate to
                    onProfileClick = {}
                )
            }
        },
        bottomBar = {
            if (selectedDestination != null) {
                AppBottomBar(
                    selectedDestination = selectedDestination,
                    onDestinationClick = { destination ->
                        navController.navigate(
                            destination.route,
                            navOptions {
                                popUpTo(HomeGraph) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ){
            loginScreen(
                onLogin = {
                    navController.navigateToHomeGraph(
                        navOptions { popUpTo(LoginDestination) {inclusive = true} }
                    )
                },
                onRegister = {},
                onLoginWithGoogle = {}
            )

            homeGraph(
                navController = navController,

                //TODO: update with diagnosis camera screen and add plant screen
                onDiagnosisClick = {},
                onAddPlantClick = {}
            )

            collectionGraph(
                navController = navController,
                onAddPlantClick = {}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSectionTopBar(
    section: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppTopBar(
        title = "",
        modifier = modifier,
        actions = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color = darkenColor(originalColor = MaterialTheme.colorScheme.surface))
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.botanika_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                        )
                    }

                    Column {
                        Text(
                            text = "Botanika",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = section,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.notification),
                        contentDescription = null
                    )

                    Image(
                        painterResource(id = R.drawable.account_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable(
                                onClick = onProfileClick
                            )
                    )
                }
            }
        }
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
                    indicatorColor = darkenColor(originalColor = MaterialTheme.colorScheme.primary),
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}