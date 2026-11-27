package cl.duoc.corpaliv.contenido.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// Programa de formación de la Escuela Jan Van Dijk
@Entity
@Table(name = "programas")
class Programa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 80)
    var nombre: String,

    @Column(nullable = false, length = 500)
    var descripcion: String,

    @Column(nullable = false, length = 40)
    var duracion: String,

    @Column(nullable = false)
    var cupos: Int
)
