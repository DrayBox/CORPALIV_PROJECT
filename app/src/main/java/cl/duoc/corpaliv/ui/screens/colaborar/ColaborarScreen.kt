package cl.duoc.corpaliv.ui.screens.colaborar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.runtime.Composable
import cl.duoc.corpaliv.ui.components.PlaceholderAction
import cl.duoc.corpaliv.ui.components.PlaceholderScreen

@Composable
fun ColaborarScreen(onGoToInteres: () -> Unit) {
    PlaceholderScreen(
        title = "Cómo colaborar",
        description = "Puedes aportar con donaciones, alianzas, voluntariado, compra de productos o capacitaciones.",
        icon = Icons.Outlined.VolunteerActivism,
        actions = listOf(PlaceholderAction("Enviar solicitud de interés", onGoToInteres))
    )
}
