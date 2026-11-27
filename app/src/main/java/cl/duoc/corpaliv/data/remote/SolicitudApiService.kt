package cl.duoc.corpaliv.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de ms-solicitudes. Retrofit genera la implementación.
// X-Usuario-Id: de quién son las solicitudes (ver SessionDataStore.idInstalacion)
interface SolicitudApiService {

    @GET("api/solicitudes")
    suspend fun listar(@Header(USUARIO) usuario: String): List<SolicitudDto>

    @POST("api/solicitudes")
    suspend fun crear(@Header(USUARIO) usuario: String, @Body solicitud: SolicitudRequestDto): SolicitudDto

    @PUT("api/solicitudes/{id}")
    suspend fun actualizar(
        @Header(USUARIO) usuario: String,
        @Path("id") id: Long,
        @Body solicitud: SolicitudRequestDto
    ): SolicitudDto

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminar(@Header(USUARIO) usuario: String, @Path("id") id: Long)

    private companion object {
        const val USUARIO = "X-Usuario-Id"
    }
}
