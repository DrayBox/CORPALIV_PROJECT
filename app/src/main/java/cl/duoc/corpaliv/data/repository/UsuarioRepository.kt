package cl.duoc.corpaliv.data.repository

import android.database.sqlite.SQLiteConstraintException
import cl.duoc.corpaliv.data.local.dao.UsuarioDao
import cl.duoc.corpaliv.data.local.entity.toDomain
import cl.duoc.corpaliv.data.local.entity.toEntity
import cl.duoc.corpaliv.data.session.SessionDataStore
import cl.duoc.corpaliv.data.model.Usuario
import cl.duoc.corpaliv.domain.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

sealed interface RegistroResultado {
    data class Exito(val usuarioId: Long) : RegistroResultado
    data object EmailEnUso : RegistroResultado
}

// Punto único de acceso a usuarios y sesión para los ViewModels
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val session: SessionDataStore
) {

    // Usuario con sesión abierta (o null). Se recalcula si cambia la sesión o la fila.
    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarioActual: Flow<Usuario?> = session.usuarioIdActual.flatMapLatest { id ->
        if (id == null) flowOf(null) else usuarioDao.observarPorId(id).map { it?.toDomain() }
    }

    val haySesion: Flow<Boolean> = session.usuarioIdActual.map { it != null }

    // Cifra la clave, guarda el usuario y deja la sesión iniciada
    suspend fun registrar(usuario: Usuario, clave: String): RegistroResultado {
        val email = usuario.email.normalizarEmail()
        if (usuarioDao.buscarPorEmail(email) != null) return RegistroResultado.EmailEnUso

        // PBKDF2 es lento a propósito: se calcula fuera del hilo principal
        val cifrada = withContext(Dispatchers.Default) { PasswordHasher.hash(clave) }
        val id = try {
            usuarioDao.insertar(usuario.copy(email = email).toEntity(cifrada.hash, cifrada.sal))
        } catch (e: SQLiteConstraintException) {
            // Otro registro con el mismo email alcanzó a entrar primero
            return RegistroResultado.EmailEnUso
        }
        session.iniciarSesion(id)
        return RegistroResultado.Exito(id)
    }

    // Devuelve true si el correo y la clave son correctos (y abre la sesión)
    suspend fun login(email: String, clave: String): Boolean {
        val usuario = usuarioDao.buscarPorEmail(email.normalizarEmail()) ?: return false
        val valida = withContext(Dispatchers.Default) {
            PasswordHasher.verificar(clave, usuario.passwordHash, usuario.passwordSalt)
        }
        if (valida) session.iniciarSesion(usuario.id)
        return valida
    }

    suspend fun idSesionActual(): Long? = session.usuarioIdActual.first()

    suspend fun cerrarSesion() = session.cerrarSesion()

    suspend fun actualizarFoto(uri: String?) {
        val id = session.usuarioIdActual.first() ?: return
        usuarioDao.actualizarFoto(id, uri)
    }

    private fun String.normalizarEmail() = trim().lowercase()
}
