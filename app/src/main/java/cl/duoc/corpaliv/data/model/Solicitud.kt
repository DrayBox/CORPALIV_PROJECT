package cl.duoc.corpaliv.data.model

import java.time.LocalDate

// usaMonto: si el formulario pide un monto; montoObligatorio: si además es obligatorio
enum class TipoColaboracion(
    val etiqueta: String,
    val usaMonto: Boolean = false,
    val montoObligatorio: Boolean = false
) {
    DONACION("Donación", usaMonto = true, montoObligatorio = true),
    ALIANZA("Alianza empresarial"),
    VOLUNTARIADO("Voluntariado"),
    COMPRA_PRODUCTOS("Compra de productos", usaMonto = true),
    CAPACITACION("Capacitación")
}

// Registro de interés que el usuario envía al equipo de vinculación
data class Solicitud(
    val id: Long = 0,
    val usuarioId: Long,
    val tipo: TipoColaboracion,
    val monto: Int?,
    val fechaContacto: LocalDate,
    val mensaje: String,
    val creadaEn: Long = System.currentTimeMillis()
)
