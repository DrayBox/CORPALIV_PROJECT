package cl.duoc.corpaliv.solicitudes.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate

// Tabla "solicitudes": registros de interés enviados desde la app
@Entity
@Table(name = "solicitudes", indexes = [Index(name = "idx_solicitudes_usuario", columnList = "usuario_id")])
class Solicitud(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "usuario_id", nullable = false, updatable = false, length = 64)
    var usuarioId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var tipo: TipoColaboracion,

    var monto: Int?,

    @Column(name = "fecha_contacto", nullable = false)
    var fechaContacto: LocalDate,

    @Column(nullable = false, length = 500)
    var mensaje: String,

    @Column(name = "creada_en", nullable = false, updatable = false)
    var creadaEn: Instant = Instant.now()
)
