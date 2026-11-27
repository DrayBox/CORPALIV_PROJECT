package cl.duoc.corpaliv.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.corpaliv.data.local.CorpalivDatabase
import cl.duoc.corpaliv.data.local.dao.SolicitudDao
import cl.duoc.corpaliv.data.local.entity.SolicitudEntity
import cl.duoc.corpaliv.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

// Copia local (Room) de las solicitudes que vienen de ms-solicitudes
@RunWith(AndroidJUnit4::class)
class SolicitudDaoTest {

    private lateinit var db: CorpalivDatabase
    private lateinit var dao: SolicitudDao
    private var usuarioA = 0L
    private var usuarioB = 0L

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CorpalivDatabase::class.java).build()
        dao = db.solicitudDao()
        usuarioA = db.usuarioDao().insertar(usuarioFicticio("a@ejemplo.cl"))
        usuarioB = db.usuarioDao().insertar(usuarioFicticio("b@ejemplo.cl"))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun cadaUsuarioVeSoloLasSuyas_ordenadasDeMasNuevaAMasAntigua() = runBlocking {
        dao.guardar(solicitud(1, usuarioA, "DONACION", creadaEn = 1_000))
        dao.guardar(solicitud(2, usuarioA, "VOLUNTARIADO", creadaEn = 2_000))
        dao.guardar(solicitud(3, usuarioB, "ALIANZA", creadaEn = 3_000))

        assertEquals(listOf(2L, 1L), dao.observarPorUsuario(usuarioA).first().map { it.id })
    }

    @Test
    fun guardar_conUnIdExistente_actualiza() = runBlocking {
        dao.guardar(solicitud(1, usuarioA, "DONACION", creadaEn = 1_000))
        dao.guardar(solicitud(1, usuarioA, "ALIANZA", creadaEn = 1_000))

        assertEquals(listOf("ALIANZA"), dao.observarPorUsuario(usuarioA).first().map { it.tipo })
    }

    @Test
    fun reemplazarDeUsuario_dejaSoloLoQueRespondioElServidor_yNoTocaAOtros() = runBlocking {
        dao.guardar(solicitud(1, usuarioA, "DONACION", creadaEn = 1_000))
        dao.guardar(solicitud(2, usuarioA, "VOLUNTARIADO", creadaEn = 2_000))
        dao.guardar(solicitud(3, usuarioB, "ALIANZA", creadaEn = 3_000))

        dao.reemplazarDeUsuario(usuarioA, listOf(solicitud(4, usuarioA, "CAPACITACION", creadaEn = 4_000)))

        assertEquals(listOf(4L), dao.observarPorUsuario(usuarioA).first().map { it.id })
        assertEquals(listOf(3L), dao.observarPorUsuario(usuarioB).first().map { it.id })
    }

    @Test
    fun eliminar_quitaLaSolicitud() = runBlocking {
        dao.guardar(solicitud(1, usuarioA, "CAPACITACION", creadaEn = 1_000))
        dao.eliminar(1)
        assertNull(dao.buscarPorId(1))
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

    private fun solicitud(id: Long, usuarioId: Long, tipo: String, creadaEn: Long) = SolicitudEntity(
        id = id,
        usuarioId = usuarioId,
        tipo = tipo,
        monto = null,
        fechaContacto = LocalDate.of(2026, 12, 2).toEpochDay(),
        mensaje = "Mensaje de prueba",
        creadaEn = creadaEn
    )
}
