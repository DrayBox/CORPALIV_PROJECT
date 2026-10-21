package cl.duoc.corpaliv

import android.app.Application
import cl.duoc.corpaliv.data.di.AppContainer

// Vive mientras la app está abierta: es el dueño del AppContainer
class CorpalivApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
