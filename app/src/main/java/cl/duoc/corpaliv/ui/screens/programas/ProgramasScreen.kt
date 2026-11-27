package cl.duoc.corpaliv.ui.screens.programas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.data.model.Programa
import cl.duoc.corpaliv.ui.components.ActualizarMientrasVisible
import cl.duoc.corpaliv.ui.components.ListaRemota
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.ProgramasViewModel

// Programas de formación que entrega ms-contenido
@Composable
fun ProgramasScreen(viewModel: ProgramasViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ActualizarMientrasVisible { viewModel.actualizar() }

    ListaRemota(
        titulo = "Programas",
        descripcion = "Formación laboral de la Escuela Jan Van Dijk y la red Alba Lab.",
        state = state,
        clave = { it.id }
    ) { ProgramaCard(it) }
}

@Composable
private fun ProgramaCard(programa: Programa) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(programa.nombre, style = MaterialTheme.typography.titleMedium)
            Text(programa.descripcion, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Dato(Icons.Outlined.Schedule, programa.duracion)
                Dato(Icons.Outlined.Groups, "${programa.cupos} cupos")
            }
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}
