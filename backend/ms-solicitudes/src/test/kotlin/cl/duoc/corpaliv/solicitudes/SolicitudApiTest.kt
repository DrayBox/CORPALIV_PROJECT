package cl.duoc.corpaliv.solicitudes

import cl.duoc.corpaliv.solicitudes.repository.SolicitudRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

// Prueba los endpoints completos (controller, service y base de datos en memoria)
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudApiTest @Autowired constructor(
    private val mvc: MockMvc,
    private val repository: SolicitudRepository
) {
    private val manana = LocalDate.now().plusDays(1)

    @BeforeEach
    fun limpiar() = repository.deleteAll()

    private fun json(
        tipo: String = "DONACION",
        monto: Int? = 50_000,
        fecha: LocalDate = manana,
        mensaje: String = "Queremos apoyar los talleres"
    ) = """{"tipo":"$tipo","monto":$monto,"fechaContacto":"$fecha","mensaje":"$mensaje"}"""

    private fun enviar(cuerpo: String, usuario: String = "ana") = mvc.post("/api/solicitudes") {
        header("X-Usuario-Id", usuario)
        contentType = MediaType.APPLICATION_JSON
        content = cuerpo
    }

    private fun crearYObtenerId(usuario: String = "ana"): Long {
        enviar(json(), usuario).andExpect { status { isCreated() } }
        return requireNotNull(repository.findByUsuarioIdOrderByCreadaEnDesc(usuario).first().id)
    }

    @Test
    fun `crea una solicitud y la devuelve en la lista`() {
        enviar(json()).andExpect {
            status { isCreated() }
            jsonPath("$.tipo") { value("DONACION") }
            jsonPath("$.monto") { value(50_000) }
        }
        mvc.get("/api/solicitudes") { header("X-Usuario-Id", "ana") }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].fechaContacto") { value(manana.toString()) }
        }
    }

    @Test
    fun `obtiene una solicitud por id`() {
        val id = crearYObtenerId()
        mvc.get("/api/solicitudes/$id") { header("X-Usuario-Id", "ana") }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
        }
    }

    @Test
    fun `actualiza una solicitud y descarta el monto si el tipo no lo usa`() {
        val id = crearYObtenerId()
        mvc.put("/api/solicitudes/$id") {
            header("X-Usuario-Id", "ana")
            contentType = MediaType.APPLICATION_JSON
            content = json(tipo = "VOLUNTARIADO", mensaje = "Ahora quiero ser voluntaria")
        }.andExpect {
            status { isOk() }
            jsonPath("$.tipo") { value("VOLUNTARIADO") }
            jsonPath("$.monto") { value(null) }
        }
        assertEquals("Ahora quiero ser voluntaria", repository.findById(id).get().mensaje)
    }

    @Test
    fun `elimina una solicitud`() {
        val id = crearYObtenerId()
        mvc.delete("/api/solicitudes/$id") { header("X-Usuario-Id", "ana") }.andExpect { status { isNoContent() } }
        assertEquals(0, repository.count())
    }

    @Test
    fun `un usuario no ve, edita ni elimina solicitudes de otro`() {
        val id = crearYObtenerId(usuario = "ana")
        mvc.get("/api/solicitudes") { header("X-Usuario-Id", "luis") }
            .andExpect { jsonPath("$.length()") { value(0) } }
        mvc.get("/api/solicitudes/$id") { header("X-Usuario-Id", "luis") }
            .andExpect { status { isNotFound() } }
        mvc.put("/api/solicitudes/$id") {
            header("X-Usuario-Id", "luis")
            contentType = MediaType.APPLICATION_JSON
            content = json()
        }.andExpect { status { isNotFound() } }
        mvc.delete("/api/solicitudes/$id") { header("X-Usuario-Id", "luis") }
            .andExpect { status { isNotFound() } }
        assertEquals(1, repository.count())
    }

    @Test
    fun `rechaza datos invalidos`() {
        enviar(json(monto = null)).andExpect {
            status { isBadRequest() }
            jsonPath("$.errores.monto") { value("Ingresa un monto") }
        }
        enviar(json(monto = 500)).andExpect { status { isBadRequest() } }
        enviar(json(mensaje = "Hola")).andExpect { status { isBadRequest() } }
        enviar(json(mensaje = "Hola           ")).andExpect { status { isBadRequest() } }
        enviar(json(fecha = LocalDate.now().minusDays(1))).andExpect { status { isBadRequest() } }
        enviar(json(fecha = LocalDate.now().plusDays(91))).andExpect { status { isBadRequest() } }
        enviar(json(monto = 10_000_001)).andExpect { status { isBadRequest() } }
        enviar(json(tipo = "OTRO")).andExpect { status { isBadRequest() } }
        enviar(json().replace("50000", "50000.7")).andExpect { status { isBadRequest() } }
        enviar(json().replace("\"DONACION\"", "0")).andExpect { status { isBadRequest() } }
        enviar("{}").andExpect {
            status { isBadRequest() }
            jsonPath("$.errores.tipo") { value("Elige cómo quieres colaborar") }
        }
        assertEquals(0, repository.count())
    }

    @Test
    fun `ignora el monto cuando el tipo no lo usa`() {
        enviar(json(tipo = "ALIANZA", monto = 500)).andExpect {
            status { isCreated() }
            jsonPath("$.monto") { value(null) }
        }
    }

    @Test
    fun `el id debe ser un numero`() {
        mvc.get("/api/solicitudes/abc") { header("X-Usuario-Id", "ana") }.andExpect {
            status { isBadRequest() }
            jsonPath("$.mensaje") { value("El valor de id no es válido") }
        }
    }

    @Test
    fun `exige el usuario`() {
        mvc.get("/api/solicitudes").andExpect {
            status { isBadRequest() }
            jsonPath("$.mensaje") { value("Falta el header X-Usuario-Id") }
        }
        mvc.get("/api/solicitudes") { header("X-Usuario-Id", " ") }.andExpect { status { isBadRequest() } }
    }
}
