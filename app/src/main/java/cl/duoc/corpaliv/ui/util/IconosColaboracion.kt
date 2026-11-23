package cl.duoc.corpaliv.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.corpaliv.data.model.TipoColaboracion

// Ícono de cada forma de colaborar (lo usan Colaborar y Mis solicitudes)
fun TipoColaboracion.icono(): ImageVector = when (this) {
    TipoColaboracion.DONACION -> Icons.Outlined.VolunteerActivism
    TipoColaboracion.ALIANZA -> Icons.Outlined.Handshake
    TipoColaboracion.VOLUNTARIADO -> Icons.Outlined.Groups
    TipoColaboracion.COMPRA_PRODUCTOS -> Icons.Outlined.Storefront
    TipoColaboracion.CAPACITACION -> Icons.Outlined.School
}
