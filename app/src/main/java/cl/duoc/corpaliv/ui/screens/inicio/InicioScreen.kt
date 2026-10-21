package cl.duoc.corpaliv.ui.screens.inicio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import cl.duoc.corpaliv.ui.components.PlaceholderAction
import cl.duoc.corpaliv.ui.components.PlaceholderScreen

@Composable
fun InicioScreen(onGoToColaborar: () -> Unit, onGoToSedes: () -> Unit) {
    PlaceholderScreen(
        title = "Inicio",
        description = "Conoce a CORPALIV, su compromiso con la discapacidad múltiple y cómo puedes colaborar.",
        icon = Icons.Outlined.Home,
        actions = listOf(
            PlaceholderAction("Quiero colaborar", onGoToColaborar),
            PlaceholderAction("Ver sedes", onGoToSedes)
        )
    )
}
