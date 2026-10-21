package cl.duoc.corpaliv.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.corpaliv.data.local.CorpalivDatabase
import cl.duoc.corpaliv.data.local.entity.UsuarioEntity
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class SolicitudRepositoryTest {

    private lateinit var db: CorpalivDatabase
    private lateinit var repository: SolicitudRepository
    private var usuarioA = 0L
    private var usuarioB = 0L

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CorpalivDatabase::class.java).build()
        repository = SolicitudRepository(db.solicitudDao())
        usuarioA = db.usuarioDao().insertar(usuarioFicticio("a@ejemplo.cl"))
        usuarioB = db.usuarioDao().insertar(usuarioFicticio("b@ejemplo.cl"))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun cadaUsuarioVeSoloLasSuyas_ordenadasDeMasNuevaAMasAntigua() = runBlocking {
        repository.guardar(solicitud(usuarioA, TipoColaboracion.DONACION, creadaEn = 1_000))
        repository.guardar(solicitud(usuarioA, TipoColaboracion.VOLUNTARIADO, creadaEn = 2_000))
        repository.guardar(solicitud(usuarioB, TipoColaboracion.ALIANZA, creadaEn = 3_000))

        val deA = repository.observarDeUsuario(usuarioA).first()

        assertEquals(listOf(TipoColaboracion.VOLUNTARIADO, TipoColaboracion.DONACION), deA.map { it.tipo })
    }

    @Test
    fun eliminar_quitaLaSolicitud() = runBlocking {
        val id = repository.guardar(solicitud(usuarioA, TipoColaboracion.CAPACITACION, creadaEn = 1_000))
        repository.eliminar(id)
        assertTrue(repository.observarDeUsuario(usuarioA).first().isEmpty())
    }

    private fun usuarioFicticio(email: String) = UsuarioEntity(
        nombre = "Persona de Prueba",
        email = email,
        edad = 30,
        fechaNacimiento = LocalDate.of(1996, 1, 10).toEpochDay(),
        tipoCuenta = "PERSONA",
        aceptaNewsletter = false,
        passwordHash = "hash",
        passwordSalt = "sal"
    )

    private fun solicitud(usuarioId: Long, tipo: TipoColaboracion, creadaEn: Long) = Solicitud(
        usuarioId = usuarioId,
        tipo = tipo,
        monto = if (tipo.usaMonto) 15_000 else null,
        fechaContacto = LocalDate.of(2026, 11, 2),
        mensaje = "Mensaje de prueba",
        creadaEn = creadaEn
    )
}
