package cl.duoc.corpaliv.domain

import cl.duoc.corpaliv.domain.validation.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ValidatorsTest {

    // Fecha fija para que las pruebas no dependan del día en que se ejecutan
    private val hoy = LocalDate.of(2026, 10, 16)

    @Test
    fun nombre_valido_conTildes() = assertNull(Validators.nombre("María José Peña"))

    @Test
    fun nombre_vacio_muyCorto_oConNumeros_daError() {
        assertEquals("Ingresa tu nombre", Validators.nombre("   "))
        assertNotNull(Validators.nombre("Jo"))
        assertEquals("Usa solo letras y espacios", Validators.nombre("Juan 23"))
    }

    @Test
    fun email_validoEInvalido() {
        assertNull(Validators.email("persona.prueba@ejemplo.cl"))
        assertNotNull(Validators.email("persona@"))
        assertNotNull(Validators.email("sin-arroba.cl"))
    }

    @Test
    fun edad_debeSerEnteroEnRango() {
        assertNull(Validators.edad("30"))
        assertEquals("La edad debe ser un número entero", Validators.edad("3o"))
        assertNotNull(Validators.edad("17"))
        assertNotNull(Validators.edad("150"))
    }

    @Test
    fun fechaNacimiento_futuraOMenorDeEdad_daError() {
        assertNull(Validators.fechaNacimiento(LocalDate.of(1996, 1, 10), hoy))
        assertEquals("La fecha no puede ser futura", Validators.fechaNacimiento(hoy.plusDays(1), hoy))
        assertNotNull(Validators.fechaNacimiento(LocalDate.of(2012, 5, 1), hoy))
        assertNotNull(Validators.fechaNacimiento(null, hoy))
    }

    @Test
    fun edad_debeCoincidirConFecha() {
        val fecha = LocalDate.of(1996, 1, 10) // 30 años al 16/10/2026
        assertNull(Validators.edadCoincideConFecha("30", fecha, hoy))
        assertNotNull(Validators.edadCoincideConFecha("25", fecha, hoy))
    }

    @Test
    fun clave_exigeLargoLetrasYNumeros() {
        assertNull(Validators.clave("prueba123"))
        assertNotNull(Validators.clave("abc12"))
        assertNotNull(Validators.clave("soloLetras"))
        assertNotNull(Validators.clave("12345678"))
        assertNotNull(Validators.clave("con espacio1"))
    }

    @Test
    fun confirmacion_yTerminos() {
        assertNull(Validators.confirmacionClave("prueba123", "prueba123"))
        assertEquals("Las claves no coinciden", Validators.confirmacionClave("prueba123", "prueba124"))
        assertNull(Validators.terminos(true))
        assertNotNull(Validators.terminos(false))
    }
}
