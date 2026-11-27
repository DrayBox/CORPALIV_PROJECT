package cl.duoc.corpaliv.data.model

// Contenido que entrega ms-contenido

data class Programa(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val duracion: String,
    val cupos: Int
)

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    // Pesos chilenos
    val precio: Int,
    val stock: Int
) {
    val agotado: Boolean get() = stock == 0
}
