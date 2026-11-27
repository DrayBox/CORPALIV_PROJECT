package cl.duoc.corpaliv.solicitudes.service

import cl.duoc.corpaliv.solicitudes.dto.SolicitudRequest
import cl.duoc.corpaliv.solicitudes.dto.SolicitudResponse
import cl.duoc.corpaliv.solicitudes.dto.toResponse
import cl.duoc.corpaliv.solicitudes.model.Solicitud
import cl.duoc.corpaliv.solicitudes.model.TipoColaboracion
import cl.duoc.corpaliv.solicitudes.repository.SolicitudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

class SolicitudNoEncontradaException : RuntimeException("La solicitud no existe")

class DatoInvalidoException(val campo: String, override val message: String) : RuntimeException(message)

// Reglas de negocio. Cada usuario solo ve y modifica sus propias solicitudes.
@Service
@Transactional
class SolicitudService(private val repository: SolicitudRepository) {

    @Transactional(readOnly = true)
    fun listar(usuarioId: String): List<SolicitudResponse> =
        repository.findByUsuarioIdOrderByCreadaEnDesc(usuarioId).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun obtener(id: Long, usuarioId: String): SolicitudResponse = buscar(id, usuarioId).toResponse()

    fun crear(usuarioId: String, request: SolicitudRequest): SolicitudResponse {
        val tipo = requireNotNull(request.tipo)
        return repository.save(
            Solicitud(
                usuarioId = usuarioId,
                tipo = tipo,
                monto = montoValido(tipo, request.monto),
                fechaContacto = fechaValida(requireNotNull(request.fechaContacto)),
                mensaje = mensajeValido(requireNotNull(request.mensaje))
            )
        ).toResponse()
    }

    fun actualizar(id: Long, usuarioId: String, request: SolicitudRequest): SolicitudResponse {
        val solicitud = buscar(id, usuarioId)
        val tipo = requireNotNull(request.tipo)
        solicitud.tipo = tipo
        solicitud.monto = montoValido(tipo, request.monto)
        solicitud.fechaContacto = fechaValida(requireNotNull(request.fechaContacto))
        solicitud.mensaje = mensajeValido(requireNotNull(request.mensaje))
        return repository.save(solicitud).toResponse()
    }

    fun eliminar(id: Long, usuarioId: String) = repository.delete(buscar(id, usuarioId))

    private fun buscar(id: Long, usuarioId: String): Solicitud =
        repository.findByIdAndUsuarioId(id, usuarioId) ?: throw SolicitudNoEncontradaException()

    // El monto depende del tipo: obligatorio, opcional o no se guarda
    private fun montoValido(tipo: TipoColaboracion, monto: Int?): Int? {
        if (!tipo.usaMonto) return null
        val error = when {
            monto == null -> if (tipo.montoObligatorio) "Ingresa un monto" else null
            monto < MONTO_MIN -> "El monto mínimo es \$1.000"
            monto > MONTO_MAX -> "El monto máximo es \$10.000.000"
            else -> null
        }
        if (error != null) throw DatoInvalidoException("monto", error)
        return monto
    }

    private fun fechaValida(fecha: LocalDate): LocalDate {
        if (fecha.isAfter(LocalDate.now().plusDays(DIAS_CONTACTO_MAX))) {
            throw DatoInvalidoException("fechaContacto", "Elige una fecha dentro de los próximos $DIAS_CONTACTO_MAX días")
        }
        return fecha
    }

    // Los espacios de los extremos no cuentan para el mínimo
    private fun mensajeValido(mensaje: String): String {
        val limpio = mensaje.trim()
        if (limpio.length < MENSAJE_MIN) {
            throw DatoInvalidoException("mensaje", "El mensaje debe tener entre $MENSAJE_MIN y 500 caracteres")
        }
        return limpio
    }

    private companion object {
        const val DIAS_CONTACTO_MAX = 90L
        const val MENSAJE_MIN = 10
        const val MONTO_MIN = 1_000
        const val MONTO_MAX = 10_000_000
    }
}
