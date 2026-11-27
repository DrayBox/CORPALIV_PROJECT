package cl.duoc.corpaliv.data.remote

import cl.duoc.corpaliv.data.model.Producto
import cl.duoc.corpaliv.data.model.Programa
import retrofit2.http.GET

// Endpoints de ms-contenido que usa la app (solo lectura: el contenido lo administra CORPALIV)
interface ContenidoApiService {

    @GET("api/programas")
    suspend fun programas(): List<ProgramaDto>

    @GET("api/productos")
    suspend fun productos(): List<ProductoDto>
}

data class ProgramaDto(val id: Long, val nombre: String, val descripcion: String, val duracion: String, val cupos: Int)

data class ProductoDto(val id: Long, val nombre: String, val descripcion: String, val precio: Int, val stock: Int)

fun ProgramaDto.toDomain() = Programa(id, nombre, descripcion, duracion, cupos)

fun ProductoDto.toDomain() = Producto(id, nombre, descripcion, precio, stock)
