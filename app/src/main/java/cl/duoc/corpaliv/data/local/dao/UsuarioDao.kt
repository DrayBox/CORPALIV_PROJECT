package cl.duoc.corpaliv.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.corpaliv.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // ABORT: si el email ya existe, Room lanza SQLiteConstraintException
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    // Flow: la UI se actualiza sola cuando cambia la fila (por ejemplo, la foto)
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Long): Flow<UsuarioEntity?>

    @Query("UPDATE usuarios SET foto_perfil_uri = :uri WHERE id = :id")
    suspend fun actualizarFoto(id: Long, uri: String?)
}
