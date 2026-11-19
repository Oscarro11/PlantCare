package plat.plantcare.app.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CatalogPlantImage(url: String, description: String, modifier: Modifier = Modifier) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant).semantics { contentDescription = description }, contentAlignment = Alignment.Center) {
        Icon(Icons.Outlined.LocalFlorist, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        AsyncImage(model = url.takeIf { it.isNotBlank() }, contentDescription = null,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}
