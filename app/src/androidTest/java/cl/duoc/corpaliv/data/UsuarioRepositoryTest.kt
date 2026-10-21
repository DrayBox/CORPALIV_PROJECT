package cl.duoc.corpaliv.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.corpaliv.data.local.CorpalivDatabase
import cl.duoc.corpaliv.data.repository.RegistroResultado
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.session.SessionDataStore
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

// Corre en el emulador: Room real (en memoria) + DataStore + PasswordHasher
@RunWith(AndroidJUnit4::class)
class UsuarioRepositoryTest {

    private lateinit var db: CorpalivDatabase
    private lateinit var session: SessionDataStore
    private lateinit var repository: UsuarioRepository

    // Datos ficticios
    private val usuario = Usuario(
        nombre = "Persona de Prueba",
        email = "Prueba@Ejemplo.cl",
        edad = 30,
        fechaNacimiento = LocalDate.of(1996, 1, 10),
        tipoCuenta = TipoCuenta.PERSONA,
        aceptaNewsletter = true
    )

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CorpalivDatabase::class.java).build()
        session = SessionDataStore(context)
        session.cerrarSesion()
        repository = UsuarioRepository(db.usuarioDao(), session)
    }

    @After
    fun tearDown() = runBlocking {
        session.cerrarSesion()
        db.close()
    }

    @Test
    fun registrar_guardaClaveCifradaYAbreSesion() = runBlocking {
        val resultado = repository.registrar(usuario, "prueba123")

        assertTrue(resultado is RegistroResultado.Exito)
        val fila = db.usuarioDao().buscarPorEmail("prueba@ejemplo.cl")!!
        assertNotEquals("prueba123", fila.passwordHash)
        assertEquals("prueba@ejemplo.cl", fila.email) // se normaliza a minúsculas
        assertEquals("Persona de Prueba", repository.usuarioActual.first()?.nombre)
    }

    @Test
    fun registrar_emailRepetido_seRechaza() = runBlocking {
        repository.registrar(usuario, "prueba123")
        val segundo = repository.registrar(usuario.copy(email = "prueba@ejemplo.cl "), "otra1234")
        assertEquals(RegistroResultado.EmailEnUso, segundo)
    }

    @Test
    fun login_correctoEIncorrecto() = runBlocking {
        repository.registrar(usuario, "prueba123")
        repository.cerrarSesion()
        assertNull(repository.usuarioActual.first())

        assertFalse(repository.login("prueba@ejemplo.cl", "clave999"))
        assertFalse(repository.haySesion.first())

        assertTrue(repository.login("PRUEBA@ejemplo.cl", "prueba123"))
        assertTrue(repository.haySesion.first())
    }
}
