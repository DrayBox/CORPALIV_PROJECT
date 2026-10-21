package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.RegistroResultado
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import cl.duoc.corpaliv.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val edad: String = "",
    val fechaNacimiento: LocalDate? = null,
    val tipoCuenta: TipoCuenta = TipoCuenta.PERSONA,
    val clave: String = "",
    val confirmacion: String = "",
    val aceptaTerminos: Boolean = false,
    val newsletter: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val edadError: String? = null,
    val fechaError: String? = null,
    val claveError: String? = null,
    val confirmacionError: String? = null,
    val terminosError: String? = null,
    val cargando: Boolean = false,
    // Sube en cada envío fallido: la UI lo usa para animar (shake)
    val intentoFallido: Int = 0,
    val exito: Boolean = false
) {
    val tieneErrores: Boolean
        get() = listOf(nombreError, emailError, edadError, fechaError, claveError, confirmacionError, terminosError)
            .any { it != null }
}

// Cada campo se valida apenas cambia (feedback inmediato) y todos juntos al enviar.
// Las reglas están en domain/validation/Validators: aquí solo se aplican.
class RegistroViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(valor: String) =
        _uiState.update { it.copy(nombre = valor, nombreError = Validators.nombre(valor)) }

    fun onEmailChange(valor: String) =
        _uiState.update { it.copy(email = valor, emailError = Validators.email(valor)) }

    fun onEdadChange(valor: String) {
        // Solo dígitos, máximo 3
        val limpio = valor.filter { it.isDigit() }.take(3)
        _uiState.update { it.copy(edad = limpio, edadError = errorEdad(limpio, it.fechaNacimiento)) }
    }

    fun onFechaChange(fecha: LocalDate) = _uiState.update {
        it.copy(
            fechaNacimiento = fecha,
            fechaError = Validators.fechaNacimiento(fecha),
            // La edad depende de la fecha: se revalida si ya se escribió
            edadError = if (it.edad.isNotEmpty()) errorEdad(it.edad, fecha) else it.edadError
        )
    }

    fun onTipoCuentaChange(tipo: TipoCuenta) = _uiState.update { it.copy(tipoCuenta = tipo) }

    fun onClaveChange(valor: String) = _uiState.update {
        it.copy(
            clave = valor,
            claveError = Validators.clave(valor),
            confirmacionError = if (it.confirmacion.isNotEmpty()) Validators.confirmacionClave(valor, it.confirmacion) else null
        )
    }

    fun onConfirmacionChange(valor: String) =
        _uiState.update { it.copy(confirmacion = valor, confirmacionError = Validators.confirmacionClave(it.clave, valor)) }

    fun onTerminosChange(valor: Boolean) =
        _uiState.update { it.copy(aceptaTerminos = valor, terminosError = Validators.terminos(valor)) }

    fun onNewsletterChange(valor: Boolean) = _uiState.update { it.copy(newsletter = valor) }

    fun registrar() {
        if (_uiState.value.cargando) return

        // Valida todo, incluso los campos que no se han tocado
        _uiState.update {
            it.copy(
                nombreError = Validators.nombre(it.nombre),
                emailError = Validators.email(it.email),
                edadError = errorEdad(it.edad, it.fechaNacimiento),
                fechaError = Validators.fechaNacimiento(it.fechaNacimiento),
                claveError = Validators.clave(it.clave),
                confirmacionError = Validators.confirmacionClave(it.clave, it.confirmacion),
                terminosError = Validators.terminos(it.aceptaTerminos)
            )
        }
        val estado = _uiState.value
        if (estado.tieneErrores) {
            _uiState.update { it.copy(intentoFallido = it.intentoFallido + 1) }
            return
        }

        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val usuario = Usuario(
                nombre = estado.nombre.trim(),
                email = estado.email.trim(),
                edad = estado.edad.toInt(),
                fechaNacimiento = requireNotNull(estado.fechaNacimiento),
                tipoCuenta = estado.tipoCuenta,
                aceptaNewsletter = estado.newsletter
            )
            when (repository.registrar(usuario, estado.clave)) {
                is RegistroResultado.Exito -> _uiState.update { it.copy(cargando = false, exito = true) }
                RegistroResultado.EmailEnUso -> _uiState.update {
                    it.copy(cargando = false, emailError = "Ya existe una cuenta con este correo", intentoFallido = it.intentoFallido + 1)
                }
            }
        }
    }

    private fun errorEdad(edad: String, fecha: LocalDate?): String? =
        Validators.edad(edad) ?: Validators.edadCoincideConFecha(edad, fecha)
}
