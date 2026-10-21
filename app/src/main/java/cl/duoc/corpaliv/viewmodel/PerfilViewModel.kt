package cl.duoc.corpaliv.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.storage.FotoPerfilStorage
import cl.duoc.corpaliv.data.model.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: UsuarioRepository,
    private val fotoStorage: FotoPerfilStorage
) : ViewModel() {

    // stateIn convierte el Flow de Room en StateFlow; se detiene 5 s después
    // de que la pantalla deja de observarlo (y sobrevive a una rotación)
    val usuario: StateFlow<Usuario?> = repository.usuarioActual.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    // Guarda la foto en el almacenamiento interno y su ruta en Room.
    // Como usuario es un Flow de Room, la pantalla se actualiza sola.
    fun guardarFoto(foto: Bitmap) {
        viewModelScope.launch {
            val ruta = fotoStorage.guardar(foto, rutaAnterior = usuario.value?.fotoPerfilUri)
            repository.actualizarFoto(ruta)
        }
    }

    fun cerrarSesion(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repository.cerrarSesion()
            alTerminar()
        }
    }
}
