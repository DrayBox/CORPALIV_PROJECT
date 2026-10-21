package cl.duoc.corpaliv.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.corpaliv.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    // Más recientes primero
    @Query("SELECT * FROM solicitudes WHERE usuario_id = :usuarioId ORDER BY creada_en DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<SolicitudEntity>>

    @Query("DELETE FROM solicitudes WHERE id = :id")
    suspend fun eliminar(id: Long)
}
