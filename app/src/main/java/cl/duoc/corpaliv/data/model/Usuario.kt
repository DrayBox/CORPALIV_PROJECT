package cl.duoc.corpaliv.data.model

import java.time.LocalDate

enum class TipoCuenta(val etiqueta: String) {
    PERSONA("Persona"),
    EMPRESA("Empresa")
}

// Usuario tal como lo usa la app. No incluye la clave: el hash y la sal
// se quedan en la capa de datos.
data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val edad: Int,
    val fechaNacimiento: LocalDate,
    val tipoCuenta: TipoCuenta,
    val aceptaNewsletter: Boolean,
    val fotoPerfilUri: String? = null
)
