package cl.duoc.corpaliv.contenido.repository

import cl.duoc.corpaliv.contenido.model.Producto
import cl.duoc.corpaliv.contenido.model.Programa
import org.springframework.data.jpa.repository.JpaRepository

// Spring Data genera las consultas (findAll, findById, save, delete...)
interface ProgramaRepository : JpaRepository<Programa, Long>

interface ProductoRepository : JpaRepository<Producto, Long>
