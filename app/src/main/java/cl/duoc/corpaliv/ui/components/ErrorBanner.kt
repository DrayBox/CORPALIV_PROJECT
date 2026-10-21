package cl.duoc.corpaliv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Aviso de error general (no de un campo). Aparece y desaparece animado.
@Composable
fun ErrorBanner(mensaje: String?, modifier: Modifier = Modifier) {
    // Se recuerda el último mensaje para mostrarlo mientras se anima la salida
    var ultimo by remember { mutableStateOf(mensaje) }
    if (mensaje != null) ultimo = mensaje

    AparecerVertical(visible = mensaje != null) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Filled.Error, contentDescription = null)
                Text(ultimo.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
