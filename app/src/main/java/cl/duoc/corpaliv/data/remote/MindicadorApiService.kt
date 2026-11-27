package cl.duoc.corpaliv.data.remote

import retrofit2.http.GET

// API externa pública mindicador.cl: indicadores económicos de Chile.
// A diferencia de los microservicios, no es nuestra: solo se lee.
interface MindicadorApiService {

    @GET("api")
    suspend fun indicadores(): IndicadoresDto
}

// Solo se mapean los campos que usa la app; Gson ignora el resto del JSON
data class IndicadoresDto(val uf: IndicadorDto, val dolar: IndicadorDto)

data class IndicadorDto(val nombre: String, val fecha: String, val valor: Double)
