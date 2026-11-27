package cl.duoc.corpaliv.contenido.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// Producto del taller Alba Lab. Precio en pesos chilenos.
@Entity
@Table(name = "productos")
class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 80)
    var nombre: String,

    @Column(nullable = false, length = 500)
    var descripcion: String,

    @Column(nullable = false)
    var precio: Int,

    @Column(nullable = false)
    var stock: Int
)
