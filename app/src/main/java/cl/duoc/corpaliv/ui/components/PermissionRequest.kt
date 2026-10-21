package cl.duoc.corpaliv.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class EstadoPermiso {
    CONCEDIDO,
    // El usuario dijo que no, pero se le puede volver a preguntar
    DENEGADO,
    // El usuario marcó "no volver a preguntar": solo se puede activar desde Ajustes
    BLOQUEADO
}

// Devuelve una función que pide el permiso (si hace falta) y avisa el resultado.
// Si el permiso ya estaba concedido, no muestra ningún diálogo.
@Composable
fun rememberPermisoLauncher(permiso: String, onResultado: (EstadoPermiso) -> Unit): () -> Unit {
    val context = LocalContext.current
    val callback by rememberUpdatedState(onResultado)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        val activity = context.findActivity()
        callback(
            when {
                concedido -> EstadoPermiso.CONCEDIDO
                activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso) ->
                    EstadoPermiso.BLOQUEADO
                else -> EstadoPermiso.DENEGADO
            }
        )
    }

    return {
        if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
            callback(EstadoPermiso.CONCEDIDO)
        } else {
            launcher.launch(permiso)
        }
    }
}

// Tarjeta que explica por qué se necesita el permiso y ofrece reintentar o ir a Ajustes
@Composable
fun PermisoDenegadoCard(
    mensaje: String,
    estado: EstadoPermiso,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.Info, contentDescription = null)
                Text(mensaje, style = MaterialTheme.typography.bodyMedium)
            }
            if (estado == EstadoPermiso.BLOQUEADO) {
                TextButton(onClick = { context.abrirAjustesDeLaApp() }, modifier = Modifier.align(Alignment.End)) {
                    Text("Abrir ajustes")
                }
            } else {
                TextButton(onClick = onReintentar, modifier = Modifier.align(Alignment.End)) {
                    Text("Permitir")
                }
            }
        }
    }
}

private fun Context.abrirAjustesDeLaApp() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
