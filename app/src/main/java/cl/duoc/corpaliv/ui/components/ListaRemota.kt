package cl.duoc.corpaliv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.corpaliv.viewmodel.ListaUiState

// Lista que viene de un microservicio: muestra carga, error de conexión o los elementos
@Composable
fun <T> ListaRemota(
    titulo: String,
    descripcion: String,
    state: ListaUiState<T>,
    clave: (T) -> Any,
    item: @Composable (T) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall)
            Text(descripcion, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            PermisoRedLocal()
            ErrorBanner(state.error)
        }
        when {
            state.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.items.isEmpty() && state.error != null -> PlaceholderScreen(
                title = "Sin conexión",
                description = "Volveremos a intentarlo en unos segundos.",
                icon = Icons.Outlined.CloudOff
            )
            else -> {
              val listState = rememberLazyListState()
              MostrarNuevosArriba(listState, state.items.firstOrNull()?.let(clave))
              LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = clave) { item(it) }
              }
            }
        }
    }
}
