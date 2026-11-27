package cl.duoc.corpaliv.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import cl.duoc.corpaliv.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

// Copia local de las solicitudes que están en ms-solicitudes (mismo id)
@Dao
interface SolicitudDao {

    // Inserta o, si el id ya existe, actualiza
    @Upsert
    suspend fun guardar(solicitud: SolicitudEntity)

    // Más recientes primero
    @Query("SELECT * FROM solicitudes WHERE usuario_id = :usuarioId ORDER BY creada_en DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun buscarPorId(id: Long): SolicitudEntity?

    @Query("DELETE FROM solicitudes WHERE id = :id")
    suspend fun eliminar(id: Long)

    @Query("DELETE FROM solicitudes WHERE usuario_id = :usuarioId")
    suspend fun eliminarDeUsuario(usuarioId: Long)

    // Deja la copia local igual a lo que respondió el microservicio
    @Transaction
    suspend fun reemplazarDeUsuario(usuarioId: Long, solicitudes: List<SolicitudEntity>) {
        eliminarDeUsuario(usuarioId)
        solicitudes.forEach { guardar(it) }
    }
}
