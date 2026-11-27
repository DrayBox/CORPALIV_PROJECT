package cl.duoc.corpaliv.data.remote

import cl.duoc.corpaliv.util.errorHttp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

// Traducción de errores de red y HTTP a mensajes para el usuario
class LlamadaRemotaTest {

    private fun falla(error: Exception): ErrorRemoto = assertThrows(ErrorRemoto::class.java) {
        kotlinx.coroutines.runBlocking { llamarMicroservicio("ms-prueba") { throw error } }
    }

    @Test
    fun siLaLlamadaFunciona_devuelveElResultado() = runTest {
        assertEquals(42, llamarMicroservicio("ms-prueba") { 42 })
    }

    @Test
    fun sinConexion_avisaQueRevisenElServicio() {
        val error = falla(IOException("timeout"))
        assertTrue(error.message.contains("ms-prueba"))
    }

    @Test
    fun error400_traeLosMensajesPorCampo() {
        val error = falla(errorHttp(400, """{"mensaje":"Revisa los datos","errores":{"monto":"Ingresa un monto"}}"""))
        assertEquals("Revisa los datos", error.message)
        assertEquals("Ingresa un monto", error.errores["monto"])
    }

    @Test
    fun error400_conCuerpoQueNoEsJson_usaMensajeGenerico() {
        val error = falla(errorHttp(400, "no es json"))
        assertEquals("Revisa los datos", error.message)
        assertTrue(error.errores.isEmpty())
    }

    @Test
    fun error404_usaElMensajeDelServidor() {
        assertEquals("La solicitud no existe", falla(errorHttp(404, """{"mensaje":"La solicitud no existe"}""")).message)
    }

    @Test
    fun otroErrorHttp_muestraElCodigo() {
        assertTrue(falla(errorHttp(500)).message.contains("500"))
    }
}
