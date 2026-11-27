package cl.duoc.corpaliv.ui.screens.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.data.model.Producto
import cl.duoc.corpaliv.ui.components.ActualizarMientrasVisible
import cl.duoc.corpaliv.ui.components.ListaRemota
import cl.duoc.corpaliv.ui.util.formatoMonto
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.CatalogoViewModel

// Productos del taller Alba Lab que entrega ms-contenido
@Composable
fun CatalogoScreen(
    onComprar: () -> Unit,
    viewModel: CatalogoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ActualizarMientrasVisible { viewModel.actualizar() }

    ListaRemota(
        titulo = "Catálogo",
        descripcion = "Productos hechos en los talleres de Alba Lab. Lo recaudado financia la formación de los estudiantes.",
        state = state,
        clave = { it.id }
    ) { ProductoCard(it, onComprar) }
}

@Composable
private fun ProductoCard(producto: Producto, onComprar: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(formatoMonto(producto.precio), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(
                        if (producto.agotado) "Agotado" else "Disponibles: ${producto.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (producto.agotado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onComprar, enabled = !producto.agotado) { Text("Lo quiero") }
            }
        }
    }
}
