package cl.duoc.corpaliv.domain

import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.domain.validation.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class InteresValidatorsTest {

    private val hoy = LocalDate.of(2026, 10, 16)

    @Test
    fun tipo_esObligatorio() {
        assertNotNull(Validators.tipoColaboracion(null))
        assertNull(Validators.tipoColaboracion(TipoColaboracion.VOLUNTARIADO))
    }

    @Test
    fun monto_obligatorio_vacioDaError() =
        assertEquals("Ingresa un monto", Validators.monto("", requerido = true))

    @Test
    fun monto_opcional_vacioEsValido() = assertNull(Validators.monto("", requerido = false))

    @Test
    fun monto_rangoYFormato() {
        assertNull(Validators.monto("15000", requerido = true))
        assertNotNull(Validators.monto("999", requerido = true))
        assertNotNull(Validators.monto("10000001", requerido = true))
        assertEquals("El monto debe ser un número entero", Validators.monto("15.000", requerido = true))
    }

    @Test
    fun fechaContacto_noPasadaYDentroDe90Dias() {
        assertNull(Validators.fechaContacto(hoy, hoy))
        assertNull(Validators.fechaContacto(hoy.plusDays(90), hoy))
        assertEquals("La fecha no puede ser pasada", Validators.fechaContacto(hoy.minusDays(1), hoy))
        assertNotNull(Validators.fechaContacto(hoy.plusDays(91), hoy))
        assertNotNull(Validators.fechaContacto(null, hoy))
    }

    @Test
    fun mensaje_largoMinimoYMaximo() {
        assertNull(Validators.mensaje("Quiero ser voluntario"))
        assertNotNull(Validators.mensaje("   hola   "))
        assertNotNull(Validators.mensaje("a".repeat(501)))
    }

    @Test
    fun soloDonacionExigeMonto() {
        val conMontoObligatorio = TipoColaboracion.entries.filter { it.montoObligatorio }
        assertEquals(listOf(TipoColaboracion.DONACION), conMontoObligatorio)
    }
}
