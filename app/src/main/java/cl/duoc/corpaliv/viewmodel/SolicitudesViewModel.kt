package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.model.Solicitud
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SolicitudesUiState {
    data object Cargando : SolicitudesUiState
    data class Listo(val solicitudes: List<Solicitud>) : SolicitudesUiState
}

class SolicitudesViewModel(
    usuarioRepository: UsuarioRepository,
    private val solicitudRepository: SolicitudRepository
) : ViewModel() {

    // Solicitudes del usuario con sesión. Como viene de un Flow de Room,
    // la lista se actualiza sola al guardar o eliminar.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SolicitudesUiState> = usuarioRepository.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(emptyList()) else solicitudRepository.observarDeUsuario(usuario.id)
        }
        .map { SolicitudesUiState.Listo(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SolicitudesUiState.Cargando)

    fun eliminar(id: Long) {
        viewModelScope.launch { solicitudRepository.eliminar(id) }
    }
}
