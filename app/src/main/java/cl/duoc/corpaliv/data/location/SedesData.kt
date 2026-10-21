package cl.duoc.corpaliv.data.location

import cl.duoc.corpaliv.data.model.Sede

// Sedes de ejemplo para el MVP: nombres y direcciones ficticias,
// con coordenadas aproximadas de cada comuna.
object SedesData {
    val sedes = listOf(
        Sede(1, "Sede Central", "Providencia", "Av. Ejemplo 123", -33.4263, -70.6110),
        Sede(2, "Escuela (sede de ejemplo)", "Ñuñoa", "Calle Ficticia 456", -33.4569, -70.5976),
        Sede(3, "Taller Alba Lab", "Santiago Centro", "Pasaje Prueba 789", -33.4489, -70.6693),
        Sede(4, "Punto de encuentro Sur", "La Florida", "Av. Referencial 1010", -33.5227, -70.5980),
        Sede(5, "Punto de encuentro Poniente", "Maipú", "Calle Modelo 2020", -33.5100, -70.7570)
    )
}
