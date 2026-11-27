package cl.duoc.corpaliv.data.di

import android.content.Context
import cl.duoc.corpaliv.BuildConfig
import cl.duoc.corpaliv.data.local.CorpalivDatabase
import cl.duoc.corpaliv.data.location.LocationProvider
import cl.duoc.corpaliv.data.remote.MindicadorApiService
import cl.duoc.corpaliv.data.remote.RetrofitClient
import cl.duoc.corpaliv.data.remote.SolicitudApiService
import cl.duoc.corpaliv.data.repository.IndicadorRepository
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.session.SessionDataStore
import cl.duoc.corpaliv.data.storage.FotoPerfilStorage

// Inyección de dependencias manual: aquí se crean una sola vez la base de datos,
// el DataStore y los repositorios. Los ViewModels los reciben desde aquí.
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: CorpalivDatabase by lazy { CorpalivDatabase.crear(appContext) }
    private val session: SessionDataStore by lazy { SessionDataStore(appContext) }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository(database.usuarioDao(), session)
    }

    val solicitudRepository: SolicitudRepository by lazy {
        SolicitudRepository(
            api = RetrofitClient.crear(BuildConfig.URL_SOLICITUDES).create(SolicitudApiService::class.java),
            dao = database.solicitudDao(),
            idRemoto = { usuarioId -> "${session.idInstalacion()}-$usuarioId" }
        )
    }

    // API externa: UF y dólar del día
    val indicadorRepository: IndicadorRepository by lazy {
        IndicadorRepository(RetrofitClient.crear(URL_MINDICADOR).create(MindicadorApiService::class.java))
    }

    val fotoPerfilStorage: FotoPerfilStorage by lazy { FotoPerfilStorage(appContext) }

    val locationProvider: LocationProvider by lazy { LocationProvider(appContext) }

    private companion object {
        const val URL_MINDICADOR = "https://mindicador.cl/"
    }
}
