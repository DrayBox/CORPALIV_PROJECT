package cl.duoc.corpaliv.solicitudes.model

// Mismos nombres que el enum de la app: se envían como texto en el JSON
enum class TipoColaboracion(val usaMonto: Boolean = false, val montoObligatorio: Boolean = false) {
    DONACION(usaMonto = true, montoObligatorio = true),
    ALIANZA,
    VOLUNTARIADO,
    COMPRA_PRODUCTOS(usaMonto = true),
    CAPACITACION
}
