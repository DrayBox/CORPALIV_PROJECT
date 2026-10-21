package cl.duoc.corpaliv.data

import cl.duoc.corpaliv.data.local.entity.toDomain
import cl.duoc.corpaliv.data.local.entity.toEntity
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

// Datos ficticios: ningún dato real de personas
class MappersTest {

    @Test
    fun usuario_idaYVuelta_conservaLosDatos() {
        val usuario = Usuario(
            id = 7,
            nombre = "Persona de Prueba",
            email = "prueba@ejemplo.cl",
            edad = 30,
            fechaNacimiento = LocalDate.of(1995, 4, 12),
            tipoCuenta = TipoCuenta.EMPRESA,
            aceptaNewsletter = true
        )

        val entity = usuario.toEntity(passwordHash = "hash", passwordSalt = "sal")

        assertEquals("EMPRESA", entity.tipoCuenta)
        assertEquals("hash", entity.passwordHash)
        assertEquals(usuario, entity.toDomain())
    }

    @Test
    fun solicitud_idaYVuelta_conservaLosDatos() {
        val solicitud = Solicitud(
            id = 3,
            usuarioId = 7,
            tipo = TipoColaboracion.VOLUNTARIADO,
            monto = null,
            fechaContacto = LocalDate.of(2026, 11, 2),
            mensaje = "Mensaje de prueba",
            creadaEn = 1_000L
        )

        assertEquals(solicitud, solicitud.toEntity().toDomain())
    }
}
