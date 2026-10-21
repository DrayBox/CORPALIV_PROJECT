package cl.duoc.corpaliv.domain.location

import cl.duoc.corpaliv.data.model.Sede
import cl.duoc.corpaliv.data.model.SedeConDistancia
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIO_TIERRA_KM = 6371.0

// Distancia en línea recta entre dos coordenadas (fórmula del haversine)
fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * RADIO_TIERRA_KM * asin(sqrt(a))
}

// Sedes ordenadas de la más cercana a la más lejana
fun ordenarPorCercania(sedes: List<Sede>, latitud: Double, longitud: Double): List<SedeConDistancia> =
    sedes.map { SedeConDistancia(it, distanciaKm(latitud, longitud, it.latitud, it.longitud)) }
        .sortedBy { it.distanciaKm }
