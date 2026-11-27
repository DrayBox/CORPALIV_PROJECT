package cl.duoc.corpaliv.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

// Error al hablar con un microservicio, con un mensaje para mostrar en pantalla.
// errores: mensajes por campo cuando el microservicio rechaza los datos (400).
class ErrorRemoto(
    override val message: String,
    val errores: Map<String, String> = emptyMap()
) : Exception(message)

// Ejecuta una llamada de Retrofit y traduce los errores de red y HTTP a ErrorRemoto
suspend fun <T> llamarMicroservicio(servicio: String, bloque: suspend () -> T): T = try {
    bloque()
} catch (e: IOException) {
    throw ErrorRemoto("No se pudo conectar con el servidor. Revisa que $servicio esté encendido.")
} catch (e: HttpException) {
    val error = leerError(e)
    throw when (e.code()) {
        400 -> ErrorRemoto(error?.mensaje ?: "Revisa los datos", error?.errores.orEmpty())
        404 -> ErrorRemoto(error?.mensaje ?: "El dato ya no existe")
        else -> ErrorRemoto("El servidor respondió con un error (${e.code()})")
    }
}

private fun leerError(e: HttpException): ErrorDto? = try {
    e.response()?.errorBody()?.string()?.let { Gson().fromJson(it, ErrorDto::class.java) }
} catch (_: Exception) {
    null
}
