package cl.duoc.corpaliv.contenido.dto

import cl.duoc.corpaliv.contenido.model.Producto
import cl.duoc.corpaliv.contenido.model.Programa
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

// Lo que se envía al crear o editar. Campos anulables para responder un mensaje claro si falta alguno.

data class ProgramaRequest(
    @field:NotBlank(message = "Ingresa el nombre")
    @field:Size(max = 80, message = "Máximo 80 caracteres")
    val nombre: String?,

    @field:NotBlank(message = "Ingresa la descripción")
    @field:Size(max = 500, message = "Máximo 500 caracteres")
    val descripcion: String?,

    @field:NotBlank(message = "Ingresa la duración")
    @field:Size(max = 40, message = "Máximo 40 caracteres")
    val duracion: String?,

    @field:NotNull(message = "Ingresa los cupos")
    @field:Min(value = 0, message = "Los cupos no pueden ser negativos")
    @field:Max(value = 500, message = "Máximo 500 cupos")
    val cupos: Int?
)

data class ProductoRequest(
    @field:NotBlank(message = "Ingresa el nombre")
    @field:Size(max = 80, message = "Máximo 80 caracteres")
    val nombre: String?,

    @field:NotBlank(message = "Ingresa la descripción")
    @field:Size(max = 500, message = "Máximo 500 caracteres")
    val descripcion: String?,

    @field:NotNull(message = "Ingresa el precio")
    @field:Min(value = 1, message = "El precio debe ser mayor que cero")
    @field:Max(value = 10_000_000, message = "El precio máximo es \$10.000.000")
    val precio: Int?,

    @field:NotNull(message = "Ingresa el stock")
    @field:Min(value = 0, message = "El stock no puede ser negativo")
    val stock: Int?
)

data class ProgramaResponse(val id: Long, val nombre: String, val descripcion: String, val duracion: String, val cupos: Int)

data class ProductoResponse(val id: Long, val nombre: String, val descripcion: String, val precio: Int, val stock: Int)

data class ErrorResponse(val mensaje: String, val errores: Map<String, String> = emptyMap())

fun Programa.toResponse() = ProgramaResponse(requireNotNull(id), nombre, descripcion, duracion, cupos)

fun Producto.toResponse() = ProductoResponse(requireNotNull(id), nombre, descripcion, precio, stock)
