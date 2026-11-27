package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.IndicadorRepository
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.domain.conversion.Indicadores
import cl.duoc.corpaliv.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class InteresUiState(
    val tipo: TipoColaboracion? = null,
    val monto: String = "",
    val fechaContacto: LocalDate? = null,
    val mensaje: String = "",
    val tipoError: String? = null,
    val montoError: String? = null,
    val fechaError: String? = null,
    val mensajeError: String? = null,
    // Error al guardar (por ejemplo, la sesión expiró)
    val errorGeneral: String? = null,
    val enviando: Boolean = false,
    // Id de la solicitud que se edita; null = solicitud nueva
    val editandoId: Long? = null,
    // UF y dólar de mindicador.cl para mostrar la equivalencia del monto (null si no cargaron)
    val indicadores: Indicadores? = null,
    // Sube en cada envío fallido: la UI lo usa para animar (shake)
    val intentoFallido: Int = 0,
    val enviada: Boolean = false
) {
    val tieneErrores: Boolean
        get() = listOf(tipoError, montoError, fechaError, mensajeError).any { it != null }
}

class InteresViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val solicitudRepository: SolicitudRepository,
    private val indicadorRepository: IndicadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InteresUiState())
    val uiState: StateFlow<InteresUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val indicadores = indicadorRepository.obtener()
            _uiState.update { it.copy(indicadores = indicadores) }
        }
    }

    // Llena el formulario con una solicitud guardada para editarla
    fun cargar(id: Long) {
        if (_uiState.value.editandoId == id) return
        viewModelScope.launch {
            val solicitud = solicitudRepository.buscar(id) ?: return@launch
            _uiState.value = InteresUiState(
                indicadores = _uiState.value.indicadores,
                tipo = solicitud.tipo,
                monto = solicitud.monto?.toString().orEmpty(),
                fechaContacto = solicitud.fechaContacto,
                mensaje = solicitud.mensaje,
                editandoId = solicitud.id
            )
        }
    }

    fun onTipoChange(tipo: TipoColaboracion) = _uiState.update {
        it.copy(
            tipo = tipo,
            tipoError = null,
            // Si el nuevo tipo no usa monto, se descarta lo escrito
            monto = if (tipo.usaMonto) it.monto else "",
            montoError = if (tipo.usaMonto && it.monto.isNotEmpty()) {
                Validators.monto(it.monto, tipo.montoObligatorio)
            } else {
                null
            }
        )
    }

    fun onMontoChange(valor: String) {
        // Solo dígitos; 8 alcanzan para el máximo de 10.000.000
        val limpio = valor.filter { it.isDigit() }.take(8)
        _uiState.update {
            it.copy(monto = limpio, montoError = Validators.monto(limpio, it.tipo?.montoObligatorio == true))
        }
    }

    fun onFechaChange(fecha: LocalDate) =
        _uiState.update { it.copy(fechaContacto = fecha, fechaError = Validators.fechaContacto(fecha)) }

    fun onMensajeChange(valor: String) {
        val recortado = valor.take(Validators.MENSAJE_MAX)
        _uiState.update { it.copy(mensaje = recortado, mensajeError = Validators.mensaje(recortado)) }
    }

    fun enviar() {
        if (_uiState.value.enviando) return

        _uiState.update {
            it.copy(
                tipoError = Validators.tipoColaboracion(it.tipo),
                montoError = if (it.tipo?.usaMonto == true) Validators.monto(it.monto, it.tipo.montoObligatorio) else null,
                fechaError = Validators.fechaContacto(it.fechaContacto),
                mensajeError = Validators.mensaje(it.mensaje),
                errorGeneral = null
            )
        }
        val estado = _uiState.value
        if (estado.tieneErrores) {
            _uiState.update { it.copy(intentoFallido = it.intentoFallido + 1) }
            return
        }

        _uiState.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val usuarioId = usuarioRepository.idSesionActual()
            if (usuarioId == null) {
                _uiState.update { it.copy(enviando = false, errorGeneral = "Tu sesión expiró. Vuelve a iniciar sesión.") }
                return@launch
            }
            val solicitud = Solicitud(
                id = estado.editandoId ?: 0,
                usuarioId = usuarioId,
                tipo = requireNotNull(estado.tipo),
                monto = estado.monto.toIntOrNull(),
                fechaContacto = requireNotNull(estado.fechaContacto),
                mensaje = estado.mensaje.trim()
            )
            try {
                // Se envía al microservicio; si responde bien, queda también en Room
                if (estado.editandoId == null) solicitudRepository.crear(solicitud) else solicitudRepository.actualizar(solicitud)
                _uiState.update { it.copy(enviando = false, enviada = true) }
            } catch (e: ErrorRemoto) {
                // Si el microservicio rechazó un campo, el error se muestra bajo ese campo
                _uiState.update {
                    it.copy(
                        enviando = false,
                        errorGeneral = e.message,
                        tipoError = e.errores["tipo"] ?: it.tipoError,
                        montoError = e.errores["monto"] ?: it.montoError,
                        fechaError = e.errores["fechaContacto"] ?: it.fechaError,
                        mensajeError = e.errores["mensaje"] ?: it.mensajeError,
                        intentoFallido = it.intentoFallido + 1
                    )
                }
            }
        }
    }
}
