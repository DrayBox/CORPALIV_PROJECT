package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.remote.ContenidoApiService
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.remote.ProductoDto
import cl.duoc.corpaliv.data.remote.ProgramaDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class ContenidoRepositoryTest {

    private val api = mockk<ContenidoApiService>()
    private val repository = ContenidoRepository(api)

    @Test
    fun programasYProductos_seConviertenAlModelo() = runTest {
        coEvery { api.programas() } returns listOf(ProgramaDto(1, "Huerto", "Cultivo", "1 año", 10))
        coEvery { api.productos() } returns listOf(ProductoDto(1, "Galletas", "Caja de 12", 4_500, 30))

        assertEquals("Huerto", repository.programas().single().nombre)
        assertEquals(4_500, repository.productos().single().precio)
    }

    @Test
    fun sinConexion_lanzaErrorRemotoQueNombraAlMicroservicio() {
        coEvery { api.productos() } throws IOException()
        val error = assertThrows(ErrorRemoto::class.java) { kotlinx.coroutines.runBlocking { repository.productos() } }
        assertEquals(true, error.message.contains("ms-contenido"))
    }
}
