package cl.duoc.corpaliv.contenido.service

import cl.duoc.corpaliv.contenido.dto.ProductoRequest
import cl.duoc.corpaliv.contenido.dto.ProductoResponse
import cl.duoc.corpaliv.contenido.dto.ProgramaRequest
import cl.duoc.corpaliv.contenido.dto.ProgramaResponse
import cl.duoc.corpaliv.contenido.dto.toResponse
import cl.duoc.corpaliv.contenido.model.Producto
import cl.duoc.corpaliv.contenido.model.Programa
import cl.duoc.corpaliv.contenido.repository.ProductoRepository
import cl.duoc.corpaliv.contenido.repository.ProgramaRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class NoEncontradoException(override val message: String) : RuntimeException(message)

@Service
@Transactional
class ProgramaService(private val repository: ProgramaRepository) {

    @Transactional(readOnly = true)
    fun listar(): List<ProgramaResponse> = repository.findAll(Sort.by("nombre")).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): ProgramaResponse = buscar(id).toResponse()

    fun crear(request: ProgramaRequest): ProgramaResponse =
        repository.save(Programa(nombre = "", descripcion = "", duracion = "", cupos = 0).aplicar(request)).toResponse()

    fun actualizar(id: Long, request: ProgramaRequest): ProgramaResponse =
        repository.save(buscar(id).aplicar(request)).toResponse()

    fun eliminar(id: Long) = repository.delete(buscar(id))

    private fun buscar(id: Long) = repository.findById(id).orElseThrow { NoEncontradoException("El programa no existe") }

    private fun Programa.aplicar(request: ProgramaRequest) = apply {
        nombre = requireNotNull(request.nombre).trim()
        descripcion = requireNotNull(request.descripcion).trim()
        duracion = requireNotNull(request.duracion).trim()
        cupos = requireNotNull(request.cupos)
    }
}

@Service
@Transactional
class ProductoService(private val repository: ProductoRepository) {

    @Transactional(readOnly = true)
    fun listar(): List<ProductoResponse> = repository.findAll(Sort.by("nombre")).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): ProductoResponse = buscar(id).toResponse()

    fun crear(request: ProductoRequest): ProductoResponse =
        repository.save(Producto(nombre = "", descripcion = "", precio = 1, stock = 0).aplicar(request)).toResponse()

    fun actualizar(id: Long, request: ProductoRequest): ProductoResponse =
        repository.save(buscar(id).aplicar(request)).toResponse()

    fun eliminar(id: Long) = repository.delete(buscar(id))

    private fun buscar(id: Long) = repository.findById(id).orElseThrow { NoEncontradoException("El producto no existe") }

    private fun Producto.aplicar(request: ProductoRequest) = apply {
        nombre = requireNotNull(request.nombre).trim()
        descripcion = requireNotNull(request.descripcion).trim()
        precio = requireNotNull(request.precio)
        stock = requireNotNull(request.stock)
    }
}
