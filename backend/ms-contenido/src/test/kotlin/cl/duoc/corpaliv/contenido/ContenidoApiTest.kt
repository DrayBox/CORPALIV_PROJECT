package cl.duoc.corpaliv.contenido

import cl.duoc.corpaliv.contenido.repository.ProductoRepository
import cl.duoc.corpaliv.contenido.repository.ProgramaRepository
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

// Prueba los endpoints completos con una base en memoria
@SpringBootTest
@AutoConfigureMockMvc
class ContenidoApiTest @Autowired constructor(
    private val mvc: MockMvc,
    private val programas: ProgramaRepository,
    private val productos: ProductoRepository
) {
    @BeforeEach
    fun limpiar() {
        programas.deleteAll()
        productos.deleteAll()
    }

    private fun programa(nombre: String = "Taller de cocina", cupos: Int = 12) =
        """{"nombre":"$nombre","descripcion":"Cocina y repostería","duracion":"6 meses","cupos":$cupos}"""

    private fun producto(nombre: String = "Galletas", precio: Int = 4500, stock: Int = 30) =
        """{"nombre":"$nombre","descripcion":"Caja de 12","precio":$precio,"stock":$stock}"""

    private fun crear(ruta: String, cuerpo: String) = mvc.post(ruta) {
        contentType = MediaType.APPLICATION_JSON
        content = cuerpo
    }

    @Test
    fun `crud completo de programas`() {
        crear("/api/programas", programa()).andExpect {
            status { isCreated() }
            jsonPath("$.nombre") { value("Taller de cocina") }
        }
        val id = requireNotNull(programas.findAll().first().id)

        mvc.get("/api/programas/$id").andExpect { jsonPath("$.cupos") { value(12) } }
        mvc.put("/api/programas/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = programa(nombre = "Taller de panadería", cupos = 8)
        }.andExpect { jsonPath("$.nombre") { value("Taller de panadería") } }
        mvc.get("/api/programas").andExpect { jsonPath("$.length()") { value(1) } }
        mvc.delete("/api/programas/$id").andExpect { status { isNoContent() } }
        assertEquals(0, programas.count())
    }

    @Test
    fun `crud completo de productos ordenados por nombre`() {
        crear("/api/productos", producto(nombre = "Libreta")).andExpect { status { isCreated() } }
        crear("/api/productos", producto(nombre = "Galletas")).andExpect { status { isCreated() } }
        mvc.get("/api/productos").andExpect {
            jsonPath("$[0].nombre") { value("Galletas") }
            jsonPath("$[1].nombre") { value("Libreta") }
        }
        val id = requireNotNull(productos.findAll().first { it.nombre == "Libreta" }.id)
        mvc.put("/api/productos/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = producto(nombre = "Libreta", precio = 7900, stock = 5)
        }.andExpect { jsonPath("$.precio") { value(7900) } }
        mvc.delete("/api/productos/$id").andExpect { status { isNoContent() } }
        assertEquals(1, productos.count())
    }

    @Test
    fun `rechaza datos invalidos`() {
        crear("/api/productos", producto(precio = 0)).andExpect {
            status { isBadRequest() }
            jsonPath("$.errores.precio") { value("El precio debe ser mayor que cero") }
        }
        crear("/api/productos", producto(stock = -1)).andExpect { status { isBadRequest() } }
        crear("/api/programas", programa(nombre = " ")).andExpect {
            status { isBadRequest() }
            jsonPath("$.errores.nombre") { value("Ingresa el nombre") }
        }
        crear("/api/programas", "{}").andExpect { status { isBadRequest() } }
        crear("/api/programas", "no es json").andExpect { status { isBadRequest() } }
        assertEquals(0, programas.count() + productos.count())
    }

    @Test
    fun `responde 404 si no existe y 400 si el id no es numero`() {
        mvc.get("/api/programas/999").andExpect {
            status { isNotFound() }
            jsonPath("$.mensaje") { value("El programa no existe") }
        }
        mvc.delete("/api/productos/999").andExpect { status { isNotFound() } }
        mvc.get("/api/productos/abc").andExpect { status { isBadRequest() } }
    }
}
