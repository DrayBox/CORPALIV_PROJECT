package cl.duoc.corpaliv.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.corpaliv.data.local.dao.SolicitudDao
import cl.duoc.corpaliv.data.local.dao.UsuarioDao
import cl.duoc.corpaliv.data.local.entity.SolicitudEntity
import cl.duoc.corpaliv.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CorpalivDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        private const val NOMBRE_BD = "corpaliv.db"

        // Se crea una sola vez desde AppContainer
        fun crear(context: Context): CorpalivDatabase =
            Room.databaseBuilder(context, CorpalivDatabase::class.java, NOMBRE_BD)
                // MVP: si cambia el esquema, se recrea la base en vez de migrar
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
