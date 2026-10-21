package cl.duoc.corpaliv.ui.screens.perfil

import android.Manifest
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.ui.components.AparecerVertical
import cl.duoc.corpaliv.ui.components.EstadoPermiso
import cl.duoc.corpaliv.ui.components.PermisoDenegadoCard
import cl.duoc.corpaliv.ui.components.rememberPermisoLauncher
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.PerfilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(
    onGoToSolicitudes: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    var estadoPermiso by rememberSaveable { mutableStateOf<EstadoPermiso?>(null) }

    // Abre la cámara del sistema y devuelve una miniatura (Bitmap)
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
        foto?.let(viewModel::guardarFoto)
    }
    // Primero el permiso de cámara; solo si se concede se abre la cámara
    val pedirCamara = rememberPermisoLauncher(Manifest.permission.CAMERA) { estado ->
        estadoPermiso = estado
        if (estado == EstadoPermiso.CONCEDIDO) camara.launch(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FotoPerfil(ruta = usuario?.fotoPerfilUri)

        Text(usuario?.nombre ?: "", style = MaterialTheme.typography.headlineSmall)
        Text(usuario?.email ?: "", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        usuario?.let { AssistChip(onClick = {}, label = { Text(it.tipoCuenta.etiqueta) }) }

        FilledTonalButton(onClick = pedirCamara, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null)
            Text(
                if (usuario?.fotoPerfilUri == null) "Tomar foto de perfil" else "Cambiar foto",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        AparecerVertical(visible = estadoPermiso != null && estadoPermiso != EstadoPermiso.CONCEDIDO) {
            PermisoDenegadoCard(
                mensaje = "Necesitamos acceso a la cámara para tomar tu foto de perfil. La foto se guarda solo en este teléfono.",
                estado = estadoPermiso ?: EstadoPermiso.DENEGADO,
                onReintentar = pedirCamara
            )
        }

        Button(onClick = onGoToSolicitudes, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Outlined.ListAlt, contentDescription = null)
            Text("Mis solicitudes", modifier = Modifier.padding(start = 8.dp))
        }
        OutlinedButton(onClick = { viewModel.cerrarSesion(onLogout) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
            Text("Cerrar sesión", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun FotoPerfil(ruta: String?) {
    // Lee el archivo fuera del hilo principal; se vuelve a leer si cambia la ruta
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta) {
        value = ruta?.let { withContext(Dispatchers.IO) { BitmapFactory.decodeFile(it)?.asImageBitmap() } }
    }
    Box(
        modifier = Modifier
            .size(128.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        imagen?.let {
            Image(it, contentDescription = "Foto de perfil", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } ?: Icon(
            Icons.Outlined.Person,
            contentDescription = "Sin foto de perfil",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(64.dp)
        )
    }
}
