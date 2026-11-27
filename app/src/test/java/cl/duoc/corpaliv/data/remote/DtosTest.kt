package cl.duoc.corpaliv.data.remote

import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.util.solicitudDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// Conversión entre el JSON de los microservicios y los modelos de la app
class DtosTest {

    @Test
    fun solicitudDto_seConvierteAlModelo_conElUsuarioLocal() {
        val solicitud = solicitudDto(id = 7, tipo = "VOLUNTARIADO", monto = null).toDomain(usuarioId = 3)
        assertEquals(7L, solicitud.id)
        assertEquals(3L, solicitud.usuarioId)
        assertEquals(TipoColaboracion.VOLUNTARIADO, solicitud.tipo)
        assertEquals(LocalDate.of(2026, 12, 1), solicitud.fechaContacto)
    }

    @Test
    fun solicitud_seEnviaConElTipoPorNombreYLaFechaComoTexto() {
        val dto = Solicitud(
            usuarioId = 1,
            tipo = TipoColaboracion.DONACION,
            monto = 15_000,
            fechaContacto = LocalDate.of(2026, 12, 24),
            mensaje = "Hola equipo"
        ).toRequestDto()
        assertEquals(SolicitudRequestDto("DONACION", 15_000, "2026-12-24", "Hola equipo"), dto)
    }

    @Test
    fun contenido_seConvierteAlModelo() {
        val programa = ProgramaDto(1, "Huerto", "Cultivo", "1 año", 10).toDomain()
        val agotado = ProductoDto(2, "Jabón", "Glicerina", 2_500, 0).toDomain()
        assertEquals("Huerto", programa.nombre)
        assertEquals(10, programa.cupos)
        assertTrue(agotado.agotado)
    }
}
