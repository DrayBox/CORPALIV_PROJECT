package cl.duoc.corpaliv.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "solicitudes". Cada solicitud pertenece a un usuario; si se borra
// el usuario, se borran sus solicitudes (CASCADE).
@Entity(
    tableName = "solicitudes",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuario_id")]
)
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Long,
    // Nombre del enum TipoColaboracion
    val tipo: String,
    val monto: Int?,
    @ColumnInfo(name = "fecha_contacto") val fechaContacto: Long,
    val mensaje: String,
    @ColumnInfo(name = "creada_en") val creadaEn: Long
)
