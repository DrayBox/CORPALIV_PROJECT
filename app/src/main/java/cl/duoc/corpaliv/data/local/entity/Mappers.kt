package cl.duoc.corpaliv.data.local.entity

import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import java.time.LocalDate

// Conversión entre las tablas de Room y los modelos del dominio

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    nombre = nombre,
    email = email,
    edad = edad,
    fechaNacimiento = LocalDate.ofEpochDay(fechaNacimiento),
    tipoCuenta = TipoCuenta.valueOf(tipoCuenta),
    aceptaNewsletter = aceptaNewsletter,
    fotoPerfilUri = fotoPerfilUri
)

fun Usuario.toEntity(passwordHash: String, passwordSalt: String) = UsuarioEntity(
    id = id,
    nombre = nombre,
    email = email,
    edad = edad,
    fechaNacimiento = fechaNacimiento.toEpochDay(),
    tipoCuenta = tipoCuenta.name,
    aceptaNewsletter = aceptaNewsletter,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt,
    fotoPerfilUri = fotoPerfilUri
)

fun SolicitudEntity.toDomain() = Solicitud(
    id = id,
    usuarioId = usuarioId,
    tipo = TipoColaboracion.valueOf(tipo),
    monto = monto,
    fechaContacto = LocalDate.ofEpochDay(fechaContacto),
    mensaje = mensaje,
    creadaEn = creadaEn
)

fun Solicitud.toEntity() = SolicitudEntity(
    id = id,
    usuarioId = usuarioId,
    tipo = tipo.name,
    monto = monto,
    fechaContacto = fechaContacto.toEpochDay(),
    mensaje = mensaje,
    creadaEn = creadaEn
)
