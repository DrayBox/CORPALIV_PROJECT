package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.local.dao.SolicitudDao
import cl.duoc.corpaliv.data.local.entity.toDomain
import cl.duoc.corpaliv.data.local.entity.toEntity
import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.remote.SolicitudApiService
import cl.duoc.corpaliv.data.remote.llamarMicroservicio
import cl.duoc.corpaliv.data.remote.toDomain
import cl.duoc.corpaliv.data.remote.toRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

// ms-solicitudes es la fuente de verdad: cada cambio se envía primero al microservicio.
// Room guarda una copia de lo que respondió, para que la pantalla se actualice sola
// (Flow) y para mostrar algo aunque se pierda la conexión.
class SolicitudRepository(
    private val api: SolicitudApiService,
    private val dao: SolicitudDao,
    // Identificador del usuario en el microservicio
    private val idRemoto: suspend (usuarioId: Long) -> String
) {

    fun observarDeUsuario(usuarioId: Long): Flow<List<Solicitud>> =
        dao.observarPorUsuario(usuarioId).map { lista -> lista.map { it.toDomain() } }

    suspend fun buscar(id: Long): Solicitud? = dao.buscarPorId(id)?.toDomain()

    // Trae las solicitudes del microservicio y reemplaza la copia local
    suspend fun sincronizar(usuarioId: Long) = llamar {
        val remotas = api.listar(idRemoto(usuarioId))
        dao.reemplazarDeUsuario(usuarioId, remotas.map { it.toDomain(usuarioId).toEntity() })
    }

    suspend fun crear(solicitud: Solicitud): Solicitud = llamar {
        val creada = api.crear(idRemoto(solicitud.usuarioId), solicitud.toRequestDto()).toDomain(solicitud.usuarioId)
        dao.guardar(creada.toEntity())
        creada
    }

    suspend fun actualizar(solicitud: Solicitud): Solicitud = llamar {
        val actualizada = api.actualizar(idRemoto(solicitud.usuarioId), solicitud.id, solicitud.toRequestDto())
            .toDomain(solicitud.usuarioId)
        dao.guardar(actualizada.toEntity())
        actualizada
    }

    suspend fun eliminar(usuarioId: Long, id: Long) = llamar {
        try {
            api.eliminar(idRemoto(usuarioId), id)
        } catch (e: HttpException) {
            // 404: ya no estaba en el microservicio; igual se quita de la copia local
            if (e.code() != 404) throw e
        }
        dao.eliminar(id)
    }

    private suspend fun <T> llamar(bloque: suspend () -> T): T = llamarMicroservicio("ms-solicitudes", bloque)
}
