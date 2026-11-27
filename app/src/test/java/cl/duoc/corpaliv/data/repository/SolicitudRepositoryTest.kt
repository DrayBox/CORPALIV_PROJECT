package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.local.dao.SolicitudDao
import cl.duoc.corpaliv.data.local.entity.SolicitudEntity
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.remote.SolicitudApiService
import cl.duoc.corpaliv.data.remote.SolicitudRequestDto
import cl.duoc.corpaliv.util.errorHttp
import cl.duoc.corpaliv.util.solicitudDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

// Se mockean la API (Retrofit) y el DAO (Room): se prueba solo la lógica del repositorio
class SolicitudRepositoryTest {

    private val api = mockk<SolicitudApiService>()
    private val dao = mockk<SolicitudDao>(relaxed = true)
    private val repository = SolicitudRepository(api, dao, idRemoto = { "instalacion-$it" })

    private val nueva = Solicitud(
        usuarioId = 3,
        tipo = TipoColaboracion.DONACION,
        monto = 50_000,
        fechaContacto = LocalDate.of(2026, 12, 1),
        mensaje = "Queremos apoyar los talleres"
    )

    @Test
    fun sincronizar_reemplazaLaCopiaLocalConLoQueRespondioElMicroservicio() = runTest {
        coEvery { api.listar("instalacion-3") } returns listOf(solicitudDto(id = 1), solicitudDto(id = 2))
        val guardadas = slot<List<SolicitudEntity>>()
        coEvery { dao.reemplazarDeUsuario(3, capture(guardadas)) } returns Unit

        repository.sincronizar(3)

        assertEquals(listOf(1L, 2L), guardadas.captured.map { it.id })
        assertEquals(3L, guardadas.captured.first().usuarioId)
    }

    @Test
    fun crear_enviaAlMicroservicioYGuardaLaRespuesta() = runTest {
        coEvery { api.crear("instalacion-3", any()) } returns solicitudDto(id = 9)

        val creada = repository.crear(nueva)

        assertEquals(9L, creada.id)
        coVerify { api.crear("instalacion-3", SolicitudRequestDto("DONACION", 50_000, "2026-12-01", "Queremos apoyar los talleres")) }
        coVerify { dao.guardar(match { it.id == 9L }) }
    }

    @Test
    fun actualizar_usaElIdDeLaSolicitud() = runTest {
        coEvery { api.actualizar("instalacion-3", 9, any()) } returns solicitudDto(id = 9, tipo = "ALIANZA", monto = null)

        val actualizada = repository.actualizar(nueva.copy(id = 9, tipo = TipoColaboracion.ALIANZA, monto = null))

        assertEquals(TipoColaboracion.ALIANZA, actualizada.tipo)
        coVerify { dao.guardar(match { it.tipo == "ALIANZA" }) }
    }

    @Test
    fun eliminar_quitaDelMicroservicioYDeLaCopiaLocal() = runTest {
        coEvery { api.eliminar("instalacion-3", 9) } returns Unit
        repository.eliminar(usuarioId = 3, id = 9)
        coVerify { dao.eliminar(9) }
    }

    @Test
    fun eliminar_siYaNoExistiaEnElServidor_igualLaQuitaDeLaCopiaLocal() = runTest {
        coEvery { api.eliminar(any(), any()) } throws errorHttp(404)
        repository.eliminar(usuarioId = 3, id = 9)
        coVerify { dao.eliminar(9) }
    }

    @Test
    fun eliminar_conOtroError_noTocaLaCopiaLocal() = runTest {
        coEvery { api.eliminar(any(), any()) } throws errorHttp(500)
        assertThrows(ErrorRemoto::class.java) { kotlinx.coroutines.runBlocking { repository.eliminar(3, 9) } }
        coVerify(exactly = 0) { dao.eliminar(any()) }
    }

    @Test
    fun sinConexion_lanzaErrorRemoto_yNoTocaRoom() = runTest {
        coEvery { api.listar(any()) } throws IOException()
        assertThrows(ErrorRemoto::class.java) { kotlinx.coroutines.runBlocking { repository.sincronizar(3) } }
        coVerify(exactly = 0) { dao.reemplazarDeUsuario(any(), any()) }
    }

    @Test
    fun observarYBuscar_leenLaCopiaLocal() = runTest {
        val entity = SolicitudEntity(1, 3, "CAPACITACION", null, LocalDate.of(2026, 12, 1).toEpochDay(), "Mensaje de prueba", 1_000)
        every { dao.observarPorUsuario(3) } returns flowOf(listOf(entity))
        coEvery { dao.buscarPorId(1) } returns entity
        coEvery { dao.buscarPorId(2) } returns null

        assertEquals(TipoColaboracion.CAPACITACION, repository.observarDeUsuario(3).first().single().tipo)
        assertEquals(1L, repository.buscar(1)?.id)
        assertNull(repository.buscar(2))
    }
}
