package cl.duoc.corpaliv.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// Desde Android 17, una app necesita permiso para hablar con otros equipos de la red local.
// Los microservicios corren en el computador (10.0.2.2 desde el emulador), así que se pide
// al entrar a las pantallas que los usan. En versiones anteriores no hace nada.
@Composable
fun PermisoRedLocal(modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < 37) return

    var estado by rememberSaveable { mutableStateOf<EstadoPermiso?>(null) }
    val pedir = rememberPermisoLauncher(Manifest.permission.ACCESS_LOCAL_NETWORK) { estado = it }

    LaunchedEffect(Unit) { if (estado == null) pedir() }

    AparecerVertical(visible = estado != null && estado != EstadoPermiso.CONCEDIDO) {
        PermisoDenegadoCard(
            mensaje = "Permite el acceso a la red local para conectarte con el servidor de CORPALIV.",
            estado = estado ?: EstadoPermiso.DENEGADO,
            onReintentar = pedir,
            modifier = modifier
        )
    }
}
