package cl.duoc.corpaliv.solicitudes.dto

import cl.duoc.corpaliv.solicitudes.model.Solicitud
import cl.duoc.corpaliv.solicitudes.model.TipoColaboracion
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

// Lo que envía la app al crear o editar. Los campos son anulables para que
// la validación responda un mensaje claro cuando falta alguno.
data class SolicitudRequest(
    @field:NotNull(message = "Elige cómo quieres colaborar")
    val tipo: TipoColaboracion?,

    // Se valida en el service porque depende del tipo
    val monto: Int?,

    @field:NotNull(message = "Elige una fecha para contactarte")
    @field:FutureOrPresent(message = "La fecha no puede ser pasada")
    val fechaContacto: LocalDate?,

    @field:NotNull(message = "Escribe un mensaje")
    @field:Size(min = 10, max = 500, message = "El mensaje debe tener entre 10 y 500 caracteres")
    val mensaje: String?
)

// Lo que responde el microservicio. No incluye el usuario: la app ya sabe cuál es.
data class SolicitudResponse(
    val id: Long,
    val tipo: TipoColaboracion,
    val monto: Int?,
    val fechaContacto: LocalDate,
    val mensaje: String,
    // Milisegundos, igual que en la app
    val creadaEn: Long
)

data class ErrorResponse(val mensaje: String, val errores: Map<String, String> = emptyMap())

fun Solicitud.toResponse() = SolicitudResponse(
    id = requireNotNull(id),
    tipo = tipo,
    monto = monto,
    fechaContacto = fechaContacto,
    mensaje = mensaje,
    creadaEn = creadaEn.toEpochMilli()
)
