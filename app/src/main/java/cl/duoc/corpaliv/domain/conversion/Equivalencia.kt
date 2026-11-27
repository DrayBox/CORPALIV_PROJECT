package cl.duoc.corpaliv.domain.conversion

import java.time.LocalDate

// Valor en pesos de la UF y el dólar en una fecha (vienen de mindicador.cl)
data class Indicadores(val uf: Double, val dolar: Double, val fecha: LocalDate)

data class Equivalencia(val uf: Double, val dolares: Double)

// Convierte un monto en pesos a UF y dólares. Función pura: se prueba con JUnit.
fun equivalencia(pesos: Int, indicadores: Indicadores): Equivalencia {
    require(indicadores.uf > 0 && indicadores.dolar > 0) { "Los indicadores deben ser mayores que cero" }
    return Equivalencia(uf = pesos / indicadores.uf, dolares = pesos / indicadores.dolar)
}
