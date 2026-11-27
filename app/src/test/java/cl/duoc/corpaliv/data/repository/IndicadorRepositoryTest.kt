package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.remote.IndicadorDto
import cl.duoc.corpaliv.data.remote.IndicadoresDto
import cl.duoc.corpaliv.data.remote.MindicadorApiService
import cl.duoc.corpaliv.util.errorHttp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

// La API externa mindicador.cl se mockea: la prueba no depende de internet
class IndicadorRepositoryTest {

    private val api = mockk<MindicadorApiService>()
    private val repository = IndicadorRepository(api)

    private val respuesta = IndicadoresDto(
        uf = IndicadorDto("Unidad de fomento (UF)", "2026-11-26T03:00:00.000Z", 41_412.34),
        dolar = IndicadorDto("Dólar observado", "2026-11-26T03:00:00.000Z", 1_044.08)
    )

    @Test
    fun obtener_convierteLaRespuesta_yLaGuardaEnMemoria() = runTest {
        coEvery { api.indicadores() } returns respuesta

        val primera = repository.obtener()
        val segunda = repository.obtener()

        assertEquals(41_412.34, primera!!.uf, 0.001)
        assertEquals(LocalDate.of(2026, 11, 26), primera.fecha)
        assertEquals(primera, segunda)
        // La segunda vez no vuelve a llamar a la API
        coVerify(exactly = 1) { api.indicadores() }
    }

    @Test
    fun sinConexion_devuelveNull_paraQueElFormularioSigaFuncionando() = runTest {
        coEvery { api.indicadores() } throws IOException()
        assertNull(repository.obtener())
    }

    @Test
    fun siLaApiFalla_devuelveNull() = runTest {
        coEvery { api.indicadores() } throws errorHttp(503)
        assertNull(repository.obtener())
    }
}
