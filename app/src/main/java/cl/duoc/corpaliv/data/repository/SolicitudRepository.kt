package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.local.dao.SolicitudDao
import cl.duoc.corpaliv.data.local.entity.toDomain
import cl.duoc.corpaliv.data.local.entity.toEntity
import cl.duoc.corpaliv.data.model.Solicitud
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SolicitudRepository(private val solicitudDao: SolicitudDao) {

    fun observarDeUsuario(usuarioId: Long): Flow<List<Solicitud>> =
        solicitudDao.observarPorUsuario(usuarioId).map { lista -> lista.map { it.toDomain() } }

    suspend fun guardar(solicitud: Solicitud): Long = solicitudDao.insertar(solicitud.toEntity())

    suspend fun eliminar(id: Long) = solicitudDao.eliminar(id)
}
