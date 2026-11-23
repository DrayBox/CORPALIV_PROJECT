package cl.duoc.corpaliv.data.model

import java.time.LocalDate

// destino: a qué se destina el aporte (se muestra en Colaborar)
// usaMonto: si el formulario pide un monto; montoObligatorio: si además es obligatorio
enum class TipoColaboracion(
    val etiqueta: String,
    val destino: String,
    val usaMonto: Boolean = false,
    val montoObligatorio: Boolean = false
) {
    DONACION("Donación", "Financia talleres de formación laboral y materiales para los estudiantes.", usaMonto = true, montoObligatorio = true),
    ALIANZA("Alianza empresarial", "Abre puestos de práctica e inclusión laboral en tu empresa."),
    VOLUNTARIADO("Voluntariado", "Apoyas talleres, salidas y actividades de la escuela."),
    COMPRA_PRODUCTOS("Compra de productos", "Lo recaudado financia la producción del taller Alba Lab.", usaMonto = true),
    CAPACITACION("Capacitación", "Tu equipo aprende a incluir a personas con discapacidad múltiple.")
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
