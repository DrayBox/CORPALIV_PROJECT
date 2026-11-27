package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.corpaliv.data.model.Producto
import cl.duoc.corpaliv.data.model.Programa
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.ContenidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estado de una lista que viene de un microservicio
data class ListaUiState<T>(
    val cargando: Boolean = true,
    val items: List<T> = emptyList(),
    // Si falla la conexión se mantiene la última lista recibida y se muestra el aviso
    val error: String? = null
)

// Base de Programas y Catálogo: la pantalla llama a actualizar() cada pocos segundos mientras está visible
abstract class ListaRemotaViewModel<T> : ViewModel() {

    private val _uiState = MutableStateFlow(ListaUiState<T>())
    val uiState: StateFlow<ListaUiState<T>> = _uiState.asStateFlow()

    protected abstract suspend fun cargar(): List<T>

    suspend fun actualizar() {
        try {
            val items = cargar()
            _uiState.update { ListaUiState(cargando = false, items = items) }
        } catch (e: ErrorRemoto) {
            _uiState.update { it.copy(cargando = false, error = e.message) }
        }
    }
}

class ProgramasViewModel(private val repository: ContenidoRepository) : ListaRemotaViewModel<Programa>() {
    override suspend fun cargar() = repository.programas()
}

class CatalogoViewModel(private val repository: ContenidoRepository) : ListaRemotaViewModel<Producto>() {
    override suspend fun cargar() = repository.productos()
}
