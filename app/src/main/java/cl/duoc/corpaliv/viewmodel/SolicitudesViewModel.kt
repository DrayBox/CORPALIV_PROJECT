package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SolicitudesUiState(
    // true hasta que termina la primera consulta al microservicio
    val cargando: Boolean = true,
    val solicitudes: List<Solicitud> = emptyList(),
    // Aviso cuando no se pudo hablar con el microservicio
    val error: String? = null
)

class SolicitudesViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val solicitudRepository: SolicitudRepository
) : ViewModel() {

    private val cargando = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)

    // La lista sale de la copia local (Room): se actualiza sola cada vez que
    // sincronizar, eliminar o el formulario guardan lo que respondió el microservicio.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val solicitudes = usuarioRepository.usuarioActual.flatMapLatest { usuario ->
        if (usuario == null) flowOf(emptyList()) else solicitudRepository.observarDeUsuario(usuario.id)
    }

    val uiState: StateFlow<SolicitudesUiState> = combine(solicitudes, cargando, error) { lista, cargando, error ->
        SolicitudesUiState(cargando = cargando, solicitudes = lista, error = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SolicitudesUiState())

    // La pantalla la llama cada pocos segundos mientras está visible:
    // así se ven los cambios hechos directo en la base de datos del microservicio.
    suspend fun sincronizar() {
        val usuarioId = usuarioRepository.idSesionActual() ?: return
        try {
            solicitudRepository.sincronizar(usuarioId)
            error.value = null
        } catch (e: ErrorRemoto) {
            error.value = "${e.message} Se muestran las últimas solicitudes guardadas."
        } finally {
            cargando.value = false
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            val usuarioId = usuarioRepository.idSesionActual() ?: return@launch
            try {
                solicitudRepository.eliminar(usuarioId, id)
                error.value = null
            } catch (e: ErrorRemoto) {
                error.value = "No se pudo eliminar. ${e.message}"
            }
        }
    }
}
