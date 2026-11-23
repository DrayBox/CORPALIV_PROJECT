package cl.duoc.corpaliv.ui.screens.colaborar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.ui.util.icono

// Las 5 formas de colaborar, cada una con el destino de su aporte.
// Al elegir una, el formulario de interés se abre con ese tipo ya seleccionado.
@Composable
fun ColaborarScreen(onGoToInteres: (TipoColaboracion) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cómo colaborar", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Elige cómo quieres aportar y mira a qué se destina tu ayuda.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TipoColaboracion.entries.forEach { tipo ->
            TarjetaModalidad(tipo, onElegir = { onGoToInteres(tipo) })
        }
    }
}

@Composable
private fun TarjetaModalidad(tipo: TipoColaboracion, onElegir: () -> Unit) {
    Card(onClick = onElegir, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(tipo.icono(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(tipo.etiqueta, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 12.dp))
            }
            Text(tipo.destino, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onElegir, modifier = Modifier.align(Alignment.End)) {
                Text("Quiero aportar así")
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}
