package cl.duoc.corpaliv.data.model

// Sede de CORPALIV (datos de ejemplo en data/location/SedesData.kt)
data class Sede(
    val id: Int,
    val nombre: String,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double
)

data class SedeConDistancia(val sede: Sede, val distanciaKm: Double)
