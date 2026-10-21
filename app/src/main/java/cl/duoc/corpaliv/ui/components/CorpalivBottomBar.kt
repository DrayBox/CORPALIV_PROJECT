package cl.duoc.corpaliv.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.corpaliv.navigation.bottomNavItems

@Composable
fun CorpalivBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        // El texto de la etiqueta ya lo lee TalkBack
                        contentDescription = null
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
