package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SPLASH_DURATION_MS = 1500L

// Decide a dónde ir después del splash: si hay sesión guardada, directo a Inicio
class SplashViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _destino = MutableStateFlow<String?>(null)
    val destino: StateFlow<String?> = _destino.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DURATION_MS)
            _destino.value = if (repository.haySesion.first()) Routes.INICIO else Routes.LOGIN
        }
    }
}
