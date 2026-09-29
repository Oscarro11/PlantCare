package plat.plantcare.app.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.darkenColor

@Preview(showBackground = true)
@Composable
fun PreviewAppScaffold(){
    FullAppScaffold(
        section = "Inicio"
    ) {}
}

enum class AppScreens (
    val label: String,
    val icon: Int
){
    INICIO(
        label = "Inicio",
        icon = R.drawable.leaf
    ),
    COLECCION(
        label = "Colección",
        icon = R.drawable.potted_plant
    ),
    CALENDARIO(
        label = "Calendario",
        icon = R.drawable.calendar
    ),
    DESCUBRIR(
        label = "Descubrir",
        icon = R.drawable.explore
    ),
    FAVORITOS(
        label = "Favoritos",
        icon = R.drawable.favorites
    )
}

@Composable
fun FullAppScaffold(
    section: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
){
    EmptyAppScaffold(
        title = "",
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

                    Column(

                    ) {
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
                        modifier = Modifier.clip(shape = CircleShape)
                    )
                }
            }
        },
        modifier = modifier
    ) { content() }
}

//TODO: add navigation with NavHost and NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyAppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    showBottomBar: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
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
        },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar()
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            content = content
        )
    }
}

@Composable
fun AppBottomBar(){
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        AppScreens.entries.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = screen.label)
                }
            )
        }
    }
}