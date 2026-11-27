package cl.duoc.corpaliv.data.repository

import cl.duoc.corpaliv.data.model.Producto
import cl.duoc.corpaliv.data.model.Programa
import cl.duoc.corpaliv.data.remote.ContenidoApiService
import cl.duoc.corpaliv.data.remote.llamarMicroservicio
import cl.duoc.corpaliv.data.remote.toDomain

// Programas y productos desde ms-contenido. Lanza ErrorRemoto si falla la conexión.
class ContenidoRepository(private val api: ContenidoApiService) {

    suspend fun programas(): List<Programa> = llamarMicroservicio(SERVICIO) { api.programas().map { it.toDomain() } }

    suspend fun productos(): List<Producto> = llamarMicroservicio(SERVICIO) { api.productos().map { it.toDomain() } }

    private companion object {
        const val SERVICIO = "ms-contenido"
    }
}
