package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.remote.MindicadorApiService
import cl.duoc.corpaliv.domain.conversion.Indicadores
import java.io.IOException
import java.time.OffsetDateTime

// Lee la UF y el dólar desde mindicador.cl. Se guardan en memoria:
// cambian una vez al día, así que basta una consulta mientras la app está abierta.
class IndicadorRepository(private val api: MindicadorApiService) {

    private var guardados: Indicadores? = null

    // null si no hay conexión o la API falla: el formulario funciona igual, sin la equivalencia
    suspend fun obtener(): Indicadores? {
        guardados?.let { return it }
        return try {
            val dto = api.indicadores()
            Indicadores(
                uf = dto.uf.valor,
                dolar = dto.dolar.valor,
                fecha = OffsetDateTime.parse(dto.uf.fecha).toLocalDate()
            ).also { guardados = it }
        } catch (e: IOException) {
            null
        } catch (e: retrofit2.HttpException) {
            null
        }
    }
}
