package cl.duoc.corpaliv.viewmodel

import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.model.Usuario
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class SolicitudesViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val usuarios = mockk<UsuarioRepository>()
    private val solicitudes = mockk<SolicitudRepository>()
    private val usuario = Usuario(3, "Ana", "ana@ejemplo.cl", 30, LocalDate.of(1996, 1, 10), TipoCuenta.PERSONA, false)
    private val lista = listOf(Solicitud(1, 3, TipoColaboracion.DONACION, 10_000, LocalDate.of(2026, 12, 1), "Mensaje de prueba"))

    private fun viewModel(): SolicitudesViewModel {
        every { usuarios.usuarioActual } returns flowOf(usuario)
        coEvery { usuarios.idSesionActual() } returns 3
        every { solicitudes.observarDeUsuario(3) } returns flowOf(lista)
        return SolicitudesViewModel(usuarios, solicitudes)
    }

    @Test
    fun sincronizar_conExito_muestraLaListaSinError() = runTest(mainRule.dispatcher) {
        coEvery { solicitudes.sincronizar(3) } returns Unit
        val vm = viewModel()
        // uiState solo se calcula mientras alguien lo observa (como la pantalla)
        backgroundScope.launch { vm.uiState.collect {} }

        assertTrue(vm.uiState.value.cargando)
        vm.sincronizar()

        assertFalse(vm.uiState.value.cargando)
        assertNull(vm.uiState.value.error)
        assertEquals(lista, vm.uiState.value.solicitudes)
    }

    @Test
    fun sincronizar_sinConexion_avisaYMantieneLaCopiaLocal() = runTest(mainRule.dispatcher) {
        coEvery { solicitudes.sincronizar(3) } throws ErrorRemoto("Sin conexión.")
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }

        vm.sincronizar()

        assertTrue(vm.uiState.value.error!!.startsWith("Sin conexión."))
        assertEquals(lista, vm.uiState.value.solicitudes)
    }

    @Test
    fun eliminar_llamaAlRepositorio_yAvisaSiFalla() = runTest(mainRule.dispatcher) {
        coEvery { solicitudes.eliminar(3, 1) } returns Unit
        coEvery { solicitudes.eliminar(3, 2) } throws ErrorRemoto("La solicitud ya no existe")
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }

        vm.eliminar(1)
        assertNull(vm.uiState.value.error)
        vm.eliminar(2)
        assertEquals("No se pudo eliminar. La solicitud ya no existe", vm.uiState.value.error)
        coVerify { solicitudes.eliminar(3, 1) }
    }

    @Test
    fun sinSesion_noConsultaElMicroservicio() = runTest(mainRule.dispatcher) {
        every { usuarios.usuarioActual } returns flowOf(null)
        coEvery { usuarios.idSesionActual() } returns null
        val vm = SolicitudesViewModel(usuarios, solicitudes)
        backgroundScope.launch { vm.uiState.collect {} }

        vm.sincronizar()
        vm.eliminar(1)

        assertTrue(vm.uiState.value.solicitudes.isEmpty())
        coVerify(exactly = 0) { solicitudes.sincronizar(any()) }
        coVerify(exactly = 0) { solicitudes.eliminar(any(), any()) }
    }
}
