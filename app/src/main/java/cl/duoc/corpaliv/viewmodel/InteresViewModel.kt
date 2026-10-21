package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
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
    // Sube en cada envío fallido: la UI lo usa para animar (shake)
    val intentoFallido: Int = 0,
    val enviada: Boolean = false
) {
    val tieneErrores: Boolean
        get() = listOf(tipoError, montoError, fechaError, mensajeError).any { it != null }
}

class InteresViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val solicitudRepository: SolicitudRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InteresUiState())
    val uiState: StateFlow<InteresUiState> = _uiState.asStateFlow()

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
            solicitudRepository.guardar(
                Solicitud(
                    usuarioId = usuarioId,
                    tipo = requireNotNull(estado.tipo),
                    monto = estado.monto.toIntOrNull(),
                    fechaContacto = requireNotNull(estado.fechaContacto),
                    mensaje = estado.mensaje.trim()
                )
            )
            _uiState.update { it.copy(enviando = false, enviada = true) }
        }
    }
}
