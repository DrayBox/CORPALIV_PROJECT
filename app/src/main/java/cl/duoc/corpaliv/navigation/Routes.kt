package cl.duoc.corpaliv.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.corpaliv.data.model.TipoColaboracion

// Nombres de las rutas en un solo lugar, para no repetir strings sueltos
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Secciones de la barra inferior
    const val INICIO = "inicio"
    const val PROGRAMAS = "programas"
    const val CATALOGO = "catalogo"
    const val COLABORAR = "colaborar"
    const val PERFIL = "perfil"

    // Pantallas secundarias (se abren desde una sección)
    const val INTERES = "interes"
    // El formulario puede abrirse con un tipo de colaboración ya elegido: "interes?tipo=DONACION"
    const val ARG_TIPO = "tipo"
    // ...o con una solicitud guardada para editarla: "interes?id=5"
    const val ARG_ID = "id"
    const val INTERES_CON_ARGS = "$INTERES?$ARG_TIPO={$ARG_TIPO}&$ARG_ID={$ARG_ID}"
    fun interes(tipo: TipoColaboracion) = "$INTERES?$ARG_TIPO=${tipo.name}"
    fun editarSolicitud(id: Long) = "$INTERES?$ARG_ID=$id"
    const val SOLICITUDES = "solicitudes"
    const val SEDES = "sedes"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.INICIO, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.PROGRAMAS, "Programas", Icons.Filled.School, Icons.Outlined.School),
    BottomNavItem(Routes.CATALOGO, "Catálogo", Icons.Filled.Storefront, Icons.Outlined.Storefront),
    BottomNavItem(Routes.COLABORAR, "Colaborar", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism),
    BottomNavItem(Routes.PERFIL, "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
)
