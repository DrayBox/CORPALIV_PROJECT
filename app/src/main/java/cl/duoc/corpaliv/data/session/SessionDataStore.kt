package cl.duoc.corpaliv.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Un único DataStore por archivo en toda la app (la delegación lo garantiza)
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")

// Guarda quién tiene la sesión abierta. Solo el id: los datos viven en Room.
class SessionDataStore(private val context: Context) {

    private object Keys {
        val USUARIO_ID = longPreferencesKey("usuario_id")
    }

    // null = no hay sesión
    val usuarioIdActual: Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.USUARIO_ID]
    }

    suspend fun iniciarSesion(usuarioId: Long) {
        context.sessionDataStore.edit { it[Keys.USUARIO_ID] = usuarioId }
    }

    suspend fun cerrarSesion() {
        context.sessionDataStore.edit { it.remove(Keys.USUARIO_ID) }
    }
}
