package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.local.dao.UsuarioDao
import cl.duoc.corpaliv.data.local.entity.UsuarioEntity
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import cl.duoc.corpaliv.data.session.SessionDataStore
import cl.duoc.corpaliv.domain.security.PasswordHasher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// Se mockean Room (DAO) y DataStore (sesión); el hash de la clave es el real
class UsuarioRepositoryTest {

    private val dao = mockk<UsuarioDao>(relaxed = true)
    private val session = mockk<SessionDataStore>(relaxed = true)

    private val usuario = Usuario(
        nombre = "Ana Pérez",
        email = "  Ana@Ejemplo.CL ",
        edad = 30,
        fechaNacimiento = LocalDate.of(1996, 1, 10),
        tipoCuenta = TipoCuenta.PERSONA,
        aceptaNewsletter = false
    )

    private fun entity(clave: String = "clave1234"): UsuarioEntity {
        val cifrada = PasswordHasher.hash(clave)
        return UsuarioEntity(5, "Ana Pérez", "ana@ejemplo.cl", 30, LocalDate.of(1996, 1, 10).toEpochDay(), "PERSONA", false, cifrada.hash, cifrada.sal)
    }

    private fun repository(): UsuarioRepository {
        every { session.usuarioIdActual } returns flowOf(5L)
        return UsuarioRepository(dao, session)
    }

    @Test
    fun registrar_guardaElCorreoNormalizado_laClaveCifrada_yAbreLaSesion() = runTest {
        coEvery { dao.buscarPorEmail("ana@ejemplo.cl") } returns null
        coEvery { dao.insertar(any()) } returns 5

        val resultado = repository().registrar(usuario, "clave1234")

        assertEquals(RegistroResultado.Exito(5), resultado)
        coVerify { dao.insertar(match { it.email == "ana@ejemplo.cl" && it.passwordHash != "clave1234" }) }
        coVerify { session.iniciarSesion(5) }
    }

    @Test
    fun registrar_conCorreoExistente_noGuarda() = runTest {
        coEvery { dao.buscarPorEmail("ana@ejemplo.cl") } returns entity()

        assertEquals(RegistroResultado.EmailEnUso, repository().registrar(usuario, "clave1234"))
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    @Test
    fun login_conClaveCorrecta_abreLaSesion() = runTest {
        coEvery { dao.buscarPorEmail("ana@ejemplo.cl") } returns entity("clave1234")

        assertTrue(repository().login(" ANA@ejemplo.cl", "clave1234"))
        coVerify { session.iniciarSesion(5) }
    }

    @Test
    fun login_conClaveIncorrectaOCorreoDesconocido_falla() = runTest {
        coEvery { dao.buscarPorEmail("ana@ejemplo.cl") } returns entity("clave1234")
        coEvery { dao.buscarPorEmail("otro@ejemplo.cl") } returns null
        val repository = repository()

        assertFalse(repository.login("ana@ejemplo.cl", "otraClave9"))
        assertFalse(repository.login("otro@ejemplo.cl", "clave1234"))
        coVerify(exactly = 0) { session.iniciarSesion(any()) }
    }

    @Test
    fun usuarioActual_yFoto_usanElIdDeLaSesion() = runTest {
        every { dao.observarPorId(5) } returns flowOf(entity())
        val repository = repository()

        assertEquals("Ana Pérez", repository.usuarioActual.first()?.nombre)
        assertTrue(repository.haySesion.first())
        assertEquals(5L, repository.idSesionActual())
        repository.actualizarFoto("foto.jpg")
        repository.cerrarSesion()
        coVerify { dao.actualizarFoto(5, "foto.jpg") }
        coVerify { session.cerrarSesion() }
    }

    @Test
    fun sinSesion_noHayUsuario() = runTest {
        every { session.usuarioIdActual } returns flowOf(null)
        val repository = UsuarioRepository(dao, session)

        assertNull(repository.usuarioActual.first())
        assertFalse(repository.haySesion.first())
        repository.actualizarFoto("foto.jpg")
        coVerify(exactly = 0) { dao.actualizarFoto(any(), any()) }
    }
}
