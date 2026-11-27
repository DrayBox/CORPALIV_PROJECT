package cl.duoc.corpaliv.data.remote

import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import java.time.LocalDate

// JSON que viaja entre la app y ms-solicitudes. La fecha va como texto "2026-12-01".

data class SolicitudRequestDto(
    val tipo: String,
    val monto: Int?,
    val fechaContacto: String,
    val mensaje: String
)

data class SolicitudDto(
    val id: Long,
    val tipo: String,
    val monto: Int?,
    val fechaContacto: String,
    val mensaje: String,
    val creadaEn: Long
)

// Respuesta de error del microservicio: {"mensaje": "...", "errores": {"monto": "..."}}
data class ErrorDto(val mensaje: String?, val errores: Map<String, String>?)

fun Solicitud.toRequestDto() = SolicitudRequestDto(
    tipo = tipo.name,
    monto = monto,
    fechaContacto = fechaContacto.toString(),
    mensaje = mensaje
)

fun SolicitudDto.toDomain(usuarioId: Long) = Solicitud(
    id = id,
    usuarioId = usuarioId,
    tipo = TipoColaboracion.valueOf(tipo),
    monto = monto,
    fechaContacto = LocalDate.parse(fechaContacto),
    mensaje = mensaje,
    creadaEn = creadaEn
)
