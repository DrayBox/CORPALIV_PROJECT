package cl.duoc.corpaliv.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.corpaliv.CorpalivApplication
import cl.duoc.corpaliv.viewmodel.LoginViewModel
import cl.duoc.corpaliv.viewmodel.RegistroViewModel
import cl.duoc.corpaliv.viewmodel.InteresViewModel
import cl.duoc.corpaliv.viewmodel.PerfilViewModel
import cl.duoc.corpaliv.viewmodel.SolicitudesViewModel
import cl.duoc.corpaliv.viewmodel.SedesViewModel
import cl.duoc.corpaliv.viewmodel.SplashViewModel

// Fábrica única de ViewModels: saca los repositorios del AppContainer
// (inyección manual, sin Hilt). Uso: viewModel(factory = AppViewModelProvider.Factory)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SplashViewModel(container().usuarioRepository) }
        initializer { LoginViewModel(container().usuarioRepository) }
        initializer { RegistroViewModel(container().usuarioRepository) }
        initializer { PerfilViewModel(container().usuarioRepository, container().fotoPerfilStorage) }
        initializer { InteresViewModel(container().usuarioRepository, container().solicitudRepository) }
        initializer { SedesViewModel(container().locationProvider) }
        initializer { SolicitudesViewModel(container().usuarioRepository, container().solicitudRepository) }
    }
}

private fun CreationExtras.container() = (this[APPLICATION_KEY] as CorpalivApplication).container
