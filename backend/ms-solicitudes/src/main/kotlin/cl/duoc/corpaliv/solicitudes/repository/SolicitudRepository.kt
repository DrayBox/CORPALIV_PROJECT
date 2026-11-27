package cl.duoc.corpaliv.solicitudes.repository

import cl.duoc.corpaliv.solicitudes.model.Solicitud
import org.springframework.data.jpa.repository.JpaRepository

// Spring Data arma las consultas a partir del nombre de cada función
interface SolicitudRepository : JpaRepository<Solicitud, Long> {

    fun findByUsuarioIdOrderByCreadaEnDesc(usuarioId: String): List<Solicitud>

    fun findByIdAndUsuarioId(id: Long, usuarioId: String): Solicitud?
}
