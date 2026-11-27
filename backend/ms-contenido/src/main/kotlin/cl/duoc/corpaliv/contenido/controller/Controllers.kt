package cl.duoc.corpaliv.contenido.controller

import cl.duoc.corpaliv.contenido.dto.ProductoRequest
import cl.duoc.corpaliv.contenido.dto.ProductoResponse
import cl.duoc.corpaliv.contenido.dto.ProgramaRequest
import cl.duoc.corpaliv.contenido.dto.ProgramaResponse
import cl.duoc.corpaliv.contenido.service.ProductoService
import cl.duoc.corpaliv.contenido.service.ProgramaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// Contenido público de la organización: cualquiera puede leerlo.
// Crear, editar y eliminar es para el equipo de CORPALIV (sin login en este MVP).

@RestController
@RequestMapping("/api/programas")
class ProgramaController(private val service: ProgramaService) {

    @GetMapping
    fun listar(): List<ProgramaResponse> = service.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: ProgramaRequest) = service.crear(request)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody request: ProgramaRequest) = service.actualizar(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}

@RestController
@RequestMapping("/api/productos")
class ProductoController(private val service: ProductoService) {

    @GetMapping
    fun listar(): List<ProductoResponse> = service.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: ProductoRequest) = service.crear(request)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody request: ProductoRequest) = service.actualizar(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}
