package cl.duoc.corpaliv.solicitudes.controller

import cl.duoc.corpaliv.solicitudes.dto.SolicitudRequest
import cl.duoc.corpaliv.solicitudes.dto.SolicitudResponse
import cl.duoc.corpaliv.solicitudes.service.SolicitudService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// El header X-Usuario-Id identifica de quién son las solicitudes.
// No es autenticación: queda como mejora para una versión productiva.
private const val USUARIO = "X-Usuario-Id"

@RestController
@RequestMapping("/api/solicitudes")
class SolicitudController(private val service: SolicitudService) {

    @GetMapping
    fun listar(@RequestHeader(USUARIO) @NotBlank @Size(max = 64) usuarioId: String): List<SolicitudResponse> =
        service.listar(usuarioId)

    @GetMapping("/{id}")
    fun obtener(
        @PathVariable id: Long,
        @RequestHeader(USUARIO) @NotBlank @Size(max = 64) usuarioId: String
    ): SolicitudResponse = service.obtener(id, usuarioId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(
        @RequestHeader(USUARIO) @NotBlank @Size(max = 64) usuarioId: String,
        @Valid @RequestBody request: SolicitudRequest
    ): SolicitudResponse = service.crear(usuarioId, request)

    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Long,
        @RequestHeader(USUARIO) @NotBlank @Size(max = 64) usuarioId: String,
        @Valid @RequestBody request: SolicitudRequest
    ): SolicitudResponse = service.actualizar(id, usuarioId, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(
        @PathVariable id: Long,
        @RequestHeader(USUARIO) @NotBlank @Size(max = 64) usuarioId: String
    ) = service.eliminar(id, usuarioId)
}
