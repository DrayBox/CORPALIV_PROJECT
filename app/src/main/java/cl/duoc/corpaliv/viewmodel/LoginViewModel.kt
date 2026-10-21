package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val clave: String = "",
    val emailError: String? = null,
    val claveError: String? = null,
    // Error que no es de un campo en particular (credenciales incorrectas)
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    // Sube en cada envío fallido: la UI lo usa para animar (shake)
    val intentoFallido: Int = 0,
    val exito: Boolean = false
)

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Al escribir se limpia el error de ese campo
    fun onEmailChange(valor: String) =
        _uiState.update { it.copy(email = valor, emailError = null, errorGeneral = null) }

    fun onClaveChange(valor: String) =
        _uiState.update { it.copy(clave = valor, claveError = null, errorGeneral = null) }

    fun iniciarSesion() {
        val estado = _uiState.value
        if (estado.cargando) return

        val emailError = Validators.requerido(estado.email, "correo") ?: Validators.email(estado.email)
        val claveError = Validators.requerido(estado.clave, "clave")
        if (emailError != null || claveError != null) {
            _uiState.update { it.copy(emailError = emailError, claveError = claveError, intentoFallido = it.intentoFallido + 1) }
            return
        }

        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val ok = repository.login(estado.email, estado.clave)
            _uiState.update {
                if (ok) {
                    it.copy(cargando = false, exito = true)
                } else {
                    // Mensaje genérico: no se revela si el correo existe
                    it.copy(cargando = false, errorGeneral = "Correo o clave incorrectos", intentoFallido = it.intentoFallido + 1)
                }
            }
        }
    }
}
