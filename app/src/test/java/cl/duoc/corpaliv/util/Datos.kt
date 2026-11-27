package cl.duoc.corpaliv.util

import cl.duoc.corpaliv.data.remote.SolicitudDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Datos ficticios reutilizados en las pruebas

fun solicitudDto(id: Long = 1, tipo: String = "DONACION", monto: Int? = 50_000) = SolicitudDto(
    id = id,
    tipo = tipo,
    monto = monto,
    fechaContacto = "2026-12-01",
    mensaje = "Queremos apoyar los talleres",
    creadaEn = 1_000L
)

// Error HTTP como el que lanza Retrofit cuando el microservicio responde con ese código
fun errorHttp(codigo: Int, cuerpo: String = "{}"): HttpException =
    HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())))
