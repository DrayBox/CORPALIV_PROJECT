package cl.duoc.corpaliv.ui.screens.inicio

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.corpaliv.ui.components.Duracion

// Cifras de ejemplo para el MVP: no son datos reales de la organización
private data class Indicador(val valor: Int, val texto: String, val icono: ImageVector)

private val indicadores = listOf(
    Indicador(120, "personas en formación laboral", Icons.Outlined.School),
    Indicador(35, "empresas aliadas", Icons.Outlined.Business),
    Indicador(8, "programas activos", Icons.Outlined.AutoStories),
    Indicador(2400, "productos Alba Lab vendidos", Icons.Outlined.Storefront)
)

@Composable
fun InicioScreen(onGoToColaborar: () -> Unit, onGoToSedes: () -> Unit) {
    // Los contadores parten en 0 y suben al abrir la pantalla
    var mostrar by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrar = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CORPALIV · Alba Lab", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(
            "Promovemos la autonomía y la inclusión social y laboral de personas con discapacidad múltiple, " +
                "a través de la Escuela Jan Van Dijk y la Red Sociolaboral Alba Lab.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Nuestro impacto", style = MaterialTheme.typography.titleMedium)
        indicadores.chunked(2).forEach { fila ->
            // IntrinsicSize.Min + fillMaxHeight: las dos tarjetas de la fila quedan de igual alto
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
                fila.forEach { TarjetaIndicador(it, mostrar, Modifier.weight(1f).fillMaxHeight()) }
            }
        }
        Text(
            "Cifras referenciales de ejemplo.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HistoriaImpacto()

        Button(onClick = onGoToColaborar, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.VolunteerActivism, contentDescription = null)
            Text("Quiero colaborar", modifier = Modifier.padding(start = 8.dp))
        }
        OutlinedButton(onClick = onGoToSedes, modifier = Modifier.fillMaxWidth()) { Text("Ver sedes") }
    }
}

@Composable
private fun TarjetaIndicador(indicador: Indicador, mostrar: Boolean, modifier: Modifier = Modifier) {
    val valor by animateIntAsState(
        targetValue = if (mostrar) indicador.valor else 0,
        animationSpec = tween(Duracion.LARGA * 2, easing = FastOutSlowInEasing),
        label = "contador"
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(indicador.icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                "%,d".format(valor).replace(',', '.'),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(indicador.texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

// Historia de impacto anonimizada (nombre y detalles ficticios)
@Composable
private fun HistoriaImpacto() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.FormatQuote, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(
                "“Después del taller de Alba Lab conseguí mi primera práctica en una empresa aliada.”",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                "Camila, 22 años · nombre ficticio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
