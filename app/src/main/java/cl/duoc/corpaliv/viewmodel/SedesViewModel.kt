package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.location.LocationProvider
import cl.duoc.corpaliv.data.location.SedesData
import cl.duoc.corpaliv.data.model.SedeConDistancia
import cl.duoc.corpaliv.domain.location.ordenarPorCercania
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SedesUiState(
    // Vacía hasta obtener la ubicación; luego, de la más cercana a la más lejana
    val ordenadas: List<SedeConDistancia> = emptyList(),
    val buscando: Boolean = false,
    val error: String? = null
)

class SedesViewModel(private val locationProvider: LocationProvider) : ViewModel() {

    private val _uiState = MutableStateFlow(SedesUiState())
    val uiState: StateFlow<SedesUiState> = _uiState.asStateFlow()

    val sedes = SedesData.sedes

    // La UI lo llama solo después de tener el permiso de ubicación
    fun buscarSedeCercana() {
        if (!locationProvider.ubicacionActivada()) {
            _uiState.update { it.copy(error = "Activa la ubicación del teléfono para encontrar la sede más cercana.") }
            return
        }
        _uiState.update { it.copy(buscando = true, error = null) }
        viewModelScope.launch {
            val ubicacion = locationProvider.ubicacionActual()
            _uiState.update {
                if (ubicacion == null) {
                    it.copy(buscando = false, error = "No pudimos obtener tu ubicación. Intenta de nuevo.")
                } else {
                    it.copy(buscando = false, ordenadas = ordenarPorCercania(sedes, ubicacion.latitude, ubicacion.longitude))
                }
            }
        }
    }
}
