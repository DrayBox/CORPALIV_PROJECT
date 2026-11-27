package cl.duoc.corpaliv.viewmodel

import cl.duoc.corpaliv.data.model.Producto
import cl.duoc.corpaliv.data.model.Programa
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.ContenidoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidoViewModelsTest {

    private val repository = mockk<ContenidoRepository>()
    private val programa = Programa(1, "Huerto", "Cultivo", "1 año", 10)
    private val producto = Producto(1, "Galletas", "Caja de 12", 4_500, 30)

    @Test
    fun programas_cargaLaLista() = runTest {
        coEvery { repository.programas() } returns listOf(programa)
        val vm = ProgramasViewModel(repository)
        assertTrue(vm.uiState.value.cargando)

        vm.actualizar()

        assertFalse(vm.uiState.value.cargando)
        assertEquals(listOf(programa), vm.uiState.value.items)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun catalogo_siSeCaeLaConexion_mantieneLosProductosYMuestraElAviso() = runTest {
        coEvery { repository.productos() } returns listOf(producto) andThenThrows ErrorRemoto("Sin conexión")
        val vm = CatalogoViewModel(repository)

        vm.actualizar()
        vm.actualizar()

        assertEquals(listOf(producto), vm.uiState.value.items)
        assertEquals("Sin conexión", vm.uiState.value.error)
    }

    @Test
    fun alVolverLaConexion_seBorraElAviso() = runTest {
        coEvery { repository.productos() } throws ErrorRemoto("Sin conexión") andThen listOf(producto)
        val vm = CatalogoViewModel(repository)

        vm.actualizar()
        assertEquals("Sin conexión", vm.uiState.value.error)
        vm.actualizar()
        assertNull(vm.uiState.value.error)
    }
}
