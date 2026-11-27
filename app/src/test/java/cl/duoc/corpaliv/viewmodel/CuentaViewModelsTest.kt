package cl.duoc.corpaliv.viewmodel

import android.graphics.Bitmap
import android.location.Location
import cl.duoc.corpaliv.data.location.LocationProvider
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.data.repository.RegistroResultado
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.data.storage.FotoPerfilStorage
import cl.duoc.corpaliv.navigation.Routes
import cl.duoc.corpaliv.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

// Login, registro, splash, perfil y sedes
@OptIn(ExperimentalCoroutinesApi::class)
class CuentaViewModelsTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val usuarios = mockk<UsuarioRepository>(relaxed = true)

    @Test
    fun login_conCamposVacios_noConsultaLaBase() {
        val vm = LoginViewModel(usuarios)
        vm.iniciarSesion()
        assertNotNull(vm.uiState.value.emailError)
        assertNotNull(vm.uiState.value.claveError)
        coVerify(exactly = 0) { usuarios.login(any(), any()) }
    }

    @Test
    fun login_correcto_eIncorrecto() {
        coEvery { usuarios.login("ana@ejemplo.cl", "clave1234") } returns true
        coEvery { usuarios.login("ana@ejemplo.cl", "mala1234") } returns false
        val vm = LoginViewModel(usuarios)
        vm.onEmailChange("ana@ejemplo.cl")

        vm.onClaveChange("mala1234")
        vm.iniciarSesion()
        assertEquals("Correo o clave incorrectos", vm.uiState.value.errorGeneral)

        vm.onClaveChange("clave1234")
        assertNull(vm.uiState.value.errorGeneral)
        vm.iniciarSesion()
        assertTrue(vm.uiState.value.exito)
    }

    private fun registroValido(): RegistroViewModel = RegistroViewModel(usuarios).apply {
        onNombreChange("Ana Pérez")
        onEmailChange("ana@ejemplo.cl")
        onFechaChange(LocalDate.now().minusYears(30))
        onTipoCuentaChange(TipoCuenta.EMPRESA)
        onClaveChange("clave1234")
        onConfirmacionChange("clave1234")
        onTerminosChange(true)
        onNewsletterChange(true)
    }

    @Test
    fun registro_conErrores_noGuarda() {
        val vm = RegistroViewModel(usuarios)
        vm.onClaveChange("clave1234")
        vm.onConfirmacionChange("otra")
        vm.onClaveChange("clave12345")
        vm.registrar()

        assertTrue(vm.uiState.value.tieneErrores)
        assertEquals(1, vm.uiState.value.intentoFallido)
        coVerify(exactly = 0) { usuarios.registrar(any(), any()) }
    }

    @Test
    fun registro_valido_calculaLaEdad_yGuarda() {
        coEvery { usuarios.registrar(any(), any()) } returns RegistroResultado.Exito(1)
        val vm = registroValido()

        assertEquals(30, vm.uiState.value.edad)
        vm.registrar()

        assertTrue(vm.uiState.value.exito)
        coVerify { usuarios.registrar(match { it.edad == 30 && it.tipoCuenta == TipoCuenta.EMPRESA && it.aceptaNewsletter }, "clave1234") }
    }

    @Test
    fun registro_conCorreoEnUso_loMarcaEnElCampo() {
        coEvery { usuarios.registrar(any(), any()) } returns RegistroResultado.EmailEnUso
        val vm = registroValido()
        vm.registrar()
        assertEquals("Ya existe una cuenta con este correo", vm.uiState.value.emailError)
    }

    @Test
    fun splash_conSesion_vaAInicio_despuesDeLaAnimacion() = runTest {
        val reloj = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(reloj)
        every { usuarios.haySesion } returns flowOf(true)

        val vm = SplashViewModel(usuarios)
        advanceTimeBy(1_000)
        assertNull(vm.destino.value)
        advanceTimeBy(600)
        assertEquals(Routes.INICIO, vm.destino.value)
    }

    @Test
    fun splash_sinSesion_vaAlLogin() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        every { usuarios.haySesion } returns flowOf(false)
        val vm = SplashViewModel(usuarios)
        advanceTimeBy(1_600)
        assertEquals(Routes.LOGIN, vm.destino.value)
    }

    @Test
    fun perfil_guardaLaFoto_yCierraSesion() = runTest(mainRule.dispatcher) {
        val storage = mockk<FotoPerfilStorage>()
        coEvery { storage.guardar(any(), any()) } returns "foto.jpg"
        every { usuarios.usuarioActual } returns flowOf(null)
        val vm = PerfilViewModel(usuarios, storage)
        backgroundScope.launch { vm.usuario.collect {} }
        var cerrada = false

        vm.guardarFoto(mockk<Bitmap>())
        vm.cerrarSesion { cerrada = true }

        coVerify { usuarios.actualizarFoto("foto.jpg") }
        coVerify { usuarios.cerrarSesion() }
        assertTrue(cerrada)
    }

    @Test
    fun sedes_conUbicacion_lasOrdenaDeMasCercanaAMasLejana() {
        val gps = mockk<LocationProvider>()
        val ubicacion = mockk<Location>()
        every { gps.ubicacionActivada() } returns true
        every { ubicacion.latitude } returns -33.45
        every { ubicacion.longitude } returns -70.66
        coEvery { gps.ubicacionActual() } returns ubicacion
        val vm = SedesViewModel(gps)

        vm.buscarSedeCercana()

        val ordenadas = vm.uiState.value.ordenadas
        assertEquals(vm.sedes.size, ordenadas.size)
        assertTrue(ordenadas.zipWithNext().all { (a, b) -> a.distanciaKm <= b.distanciaKm })
    }

    @Test
    fun sedes_sinGpsOSinUbicacion_muestraUnError() {
        val gps = mockk<LocationProvider>()
        every { gps.ubicacionActivada() } returns false andThen true
        coEvery { gps.ubicacionActual() } returns null
        val vm = SedesViewModel(gps)

        vm.buscarSedeCercana()
        assertNotNull(vm.uiState.value.error)
        vm.buscarSedeCercana()
        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.ordenadas.isEmpty())
    }
}
