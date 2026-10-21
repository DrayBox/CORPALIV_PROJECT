package cl.duoc.corpaliv.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "usuarios". El email es único: no se pueden registrar dos cuentas iguales.
@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val edad: Int,
    // Fechas como LocalDate.toEpochDay(): un número simple que SQLite ordena bien
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: Long,
    // Nombre del enum TipoCuenta
    @ColumnInfo(name = "tipo_cuenta") val tipoCuenta: String,
    @ColumnInfo(name = "acepta_newsletter") val aceptaNewsletter: Boolean,
    // Clave cifrada: hash PBKDF2 + sal aleatoria, ambos en Base64
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "password_salt") val passwordSalt: String,
    @ColumnInfo(name = "foto_perfil_uri") val fotoPerfilUri: String? = null
)
