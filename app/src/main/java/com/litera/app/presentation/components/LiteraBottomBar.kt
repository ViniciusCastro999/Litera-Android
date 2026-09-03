package com.litera.app.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.litera.app.core.navigation.Screen
import com.litera.app.presentation.components.icons.PhosphorIcons

private data class BottomBarItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val bottomBarItems = listOf(
    BottomBarItem(Screen.Home, "Home", PhosphorIcons.House),
    BottomBarItem(Screen.Explore, "Explorar", PhosphorIcons.MagnifyingGlass),
    BottomBarItem(Screen.Shelf, "Estante", PhosphorIcons.Books),
    BottomBarItem(Screen.Community, "Comunidade", PhosphorIcons.Community),
    BottomBarItem(Screen.Profile, "Perfil", PhosphorIcons.UserCircle)
)

@Composable
fun LiteraBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar {
        bottomBarItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}
