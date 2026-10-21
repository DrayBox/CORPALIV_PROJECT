package cl.duoc.corpaliv.domain

import cl.duoc.corpaliv.data.model.Sede
import cl.duoc.corpaliv.domain.location.distanciaKm
import cl.duoc.corpaliv.domain.location.ordenarPorCercania
import org.junit.Assert.assertEquals
import org.junit.Test

class SedeTest {

    @Test
    fun distancia_mismoPunto_esCero() = assertEquals(0.0, distanciaKm(-33.45, -70.66, -33.45, -70.66), 0.0001)

    @Test
    fun distancia_santiagoAValparaiso_ronda100Km() {
        // Plaza de Armas de Santiago -> Valparaíso: ~98 km en línea recta
        assertEquals(98.0, distanciaKm(-33.4378, -70.6505, -33.0472, -71.6127), 3.0)
    }

    @Test
    fun ordenarPorCercania_dejaPrimeroLaMasCercana() {
        val sedes = listOf(
            Sede(1, "Lejos", "Maipú", "Ficticia 1", -33.5100, -70.7570),
            Sede(2, "Cerca", "Providencia", "Ficticia 2", -33.4263, -70.6110)
        )
        val ordenadas = ordenarPorCercania(sedes, latitud = -33.4250, longitud = -70.6150)
        assertEquals(listOf("Cerca", "Lejos"), ordenadas.map { it.sede.nombre })
    }
}
