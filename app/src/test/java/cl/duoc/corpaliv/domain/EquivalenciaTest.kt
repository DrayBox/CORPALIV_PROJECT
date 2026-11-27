package cl.duoc.corpaliv.domain

import cl.duoc.corpaliv.domain.conversion.Indicadores
import cl.duoc.corpaliv.domain.conversion.equivalencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class EquivalenciaTest {

    private val indicadores = Indicadores(uf = 40_000.0, dolar = 1_000.0, fecha = LocalDate.of(2026, 11, 26))

    @Test
    fun convierteLosPesosAUfYDolares() {
        val eq = equivalencia(80_000, indicadores)
        assertEquals(2.0, eq.uf, 0.0001)
        assertEquals(80.0, eq.dolares, 0.0001)
    }

    @Test
    fun rechazaIndicadoresEnCero() {
        assertThrows(IllegalArgumentException::class.java) { equivalencia(1_000, indicadores.copy(uf = 0.0)) }
    }
}
