package cl.duoc.corpaliv.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.ui.components.PlaceholderAction
import cl.duoc.corpaliv.ui.components.PlaceholderScreen
import cl.duoc.corpaliv.ui.util.formatoFecha
import cl.duoc.corpaliv.ui.util.formatoMonto
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.SolicitudesUiState
import cl.duoc.corpaliv.viewmodel.SolicitudesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    onBack: () -> Unit,
    onNuevaSolicitud: () -> Unit,
    viewModel: SolicitudesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Id de la solicitud que se quiere eliminar (abre el diálogo de confirmación)
    var porEliminar by rememberSaveable { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Mis solicitudes") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            windowInsets = WindowInsets(0.dp)
        )

        when (val s = state) {
            SolicitudesUiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is SolicitudesUiState.Listo -> if (s.solicitudes.isEmpty()) {
                PlaceholderScreen(
                    title = "Aún no tienes solicitudes",
                    description = "Cuando envíes una solicitud de interés, la verás aquí.",
                    icon = Icons.AutoMirrored.Outlined.ListAlt,
                    actions = listOf(PlaceholderAction("Enviar una solicitud", onNuevaSolicitud))
                )
            } else {
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        // Espacio abajo para que el botón flotante no tape la última tarjeta
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.solicitudes, key = { it.id }) { solicitud ->
                            // animateItem: al eliminar, las demás tarjetas suben animadas
                            SolicitudCard(
                                solicitud,
                                onEliminar = { porEliminar = solicitud.id },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = onNuevaSolicitud,
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text("Nueva solicitud") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    porEliminar?.let { id ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
            title = { Text("¿Eliminar solicitud?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(id)
                    porEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SolicitudCard(solicitud: Solicitud, onEliminar: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = solicitud.tipo.icono(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    solicitud.tipo.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar solicitud de ${solicitud.tipo.etiqueta}")
                }
            }
            solicitud.monto?.let { DatoConIcono(Icons.Outlined.Payments, formatoMonto(it)) }
            DatoConIcono(Icons.Outlined.Event, "Contacto: ${formatoFecha(solicitud.fechaContacto)}")
            Text(
                solicitud.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Enviada el ${formatoFecha(solicitud.creadaEn)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DatoConIcono(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun TipoColaboracion.icono(): ImageVector = when (this) {
    TipoColaboracion.DONACION -> Icons.Outlined.VolunteerActivism
    TipoColaboracion.ALIANZA -> Icons.Outlined.Handshake
    TipoColaboracion.VOLUNTARIADO -> Icons.Outlined.Groups
    TipoColaboracion.COMPRA_PRODUCTOS -> Icons.Outlined.Storefront
    TipoColaboracion.CAPACITACION -> Icons.Outlined.School
}
