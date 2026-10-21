package cl.duoc.corpaliv.ui.screens.sedes

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.data.model.Sede
import cl.duoc.corpaliv.ui.components.AparecerVertical
import cl.duoc.corpaliv.ui.components.ErrorBanner
import cl.duoc.corpaliv.ui.components.EstadoPermiso
import cl.duoc.corpaliv.ui.components.PermisoDenegadoCard
import cl.duoc.corpaliv.ui.components.rememberPermisoLauncher
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.SedesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SedesScreen(
    onBack: () -> Unit,
    viewModel: SedesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var estadoPermiso by rememberSaveable { mutableStateOf<EstadoPermiso?>(null) }

    // Solo ubicación aproximada: basta para saber qué sede está más cerca
    val pedirUbicacion = rememberPermisoLauncher(Manifest.permission.ACCESS_COARSE_LOCATION) { estado ->
        estadoPermiso = estado
        if (estado == EstadoPermiso.CONCEDIDO) viewModel.buscarSedeCercana()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Sedes") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            windowInsets = WindowInsets(0.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Encuentra la sede de CORPALIV más cercana a ti.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Button(onClick = pedirUbicacion, enabled = !state.buscando, modifier = Modifier.fillMaxWidth()) {
                    if (state.buscando) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.MyLocation, contentDescription = null)
                        Text("Usar mi ubicación", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            item {
                AparecerVertical(visible = estadoPermiso != null && estadoPermiso != EstadoPermiso.CONCEDIDO) {
                    PermisoDenegadoCard(
                        mensaje = "Necesitamos tu ubicación aproximada para mostrarte la sede más cercana. No la guardamos.",
                        estado = estadoPermiso ?: EstadoPermiso.DENEGADO,
                        onReintentar = pedirUbicacion
                    )
                }
            }
            item { ErrorBanner(state.error) }

            if (state.ordenadas.isNotEmpty()) {
                items(state.ordenadas, key = { it.sede.id }) { item ->
                    // animateItem: al ordenar por cercanía, las tarjetas se mueven animadas
                    SedeCard(
                        item.sede,
                        distanciaKm = item.distanciaKm,
                        masCercana = item == state.ordenadas.first(),
                        modifier = Modifier.animateItem()
                    )
                }
            } else {
                items(viewModel.sedes, key = { it.id }) { sede ->
                    SedeCard(sede, distanciaKm = null, masCercana = false, modifier = Modifier.animateItem())
                }
            }
            item {
                Text(
                    "Sedes y direcciones de ejemplo para esta versión de la app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SedeCard(sede: Sede, distanciaKm: Double?, masCercana: Boolean, modifier: Modifier = Modifier) {
    Card(
        colors = if (masCercana) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                if (masCercana) {
                    Text("Más cercana", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                }
                Text(sede.nombre, style = MaterialTheme.typography.titleMedium)
                Text("${sede.direccion}, ${sede.comuna}", style = MaterialTheme.typography.bodyMedium)
            }
            distanciaKm?.let {
                Text(String.format(Locale.forLanguageTag("es-CL"), "%.1f km", it), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
