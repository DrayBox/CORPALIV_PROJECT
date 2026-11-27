package cl.duoc.corpaliv.viewmodel

import cl.duoc.corpaliv.data.model.Solicitud
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.data.remote.ErrorRemoto
import cl.duoc.corpaliv.data.repository.IndicadorRepository
import cl.duoc.corpaliv.data.repository.SolicitudRepository
import cl.duoc.corpaliv.data.repository.UsuarioRepository
import cl.duoc.corpaliv.domain.conversion.Indicadores
import cl.duoc.corpaliv.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

// Formulario de interés: validaciones, envío al microservicio y edición
class InteresViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val usuarios = mockk<UsuarioRepository>()
    private val solicitudes = mockk<SolicitudRepository>()
    private val indicadoresRepo = mockk<IndicadorRepository>()
    private val manana = LocalDate.now().plusDays(1)
    private val indicadores = Indicadores(40_000.0, 1_000.0, LocalDate.now())

    private lateinit var viewModel: InteresViewModel

    @Before
    fun setUp() {
        coEvery { indicadoresRepo.obtener() } returns indicadores
        coEvery { usuarios.idSesionActual() } returns 3
        viewModel = InteresViewModel(usuarios, solicitudes, indicadoresRepo)
    }

    private fun llenarDonacion() {
        viewModel.onTipoChange(TipoColaboracion.DONACION)
        viewModel.onMontoChange("50.000")
        viewModel.onFechaChange(manana)
        viewModel.onMensajeChange("Queremos apoyar los talleres")
    }

    @Test
    fun alAbrir_cargaLosIndicadoresDeLaApiExterna() {
        assertEquals(indicadores, viewModel.uiState.value.indicadores)
    }

    @Test
    fun enviarVacio_marcaTodosLosErrores_yNoLlamaAlMicroservicio() {
        viewModel.enviar()

        val estado = viewModel.uiState.value
        assertNotNull(estado.tipoError)
        assertNotNull(estado.fechaError)
        assertNotNull(estado.mensajeError)
        assertEquals(1, estado.intentoFallido)
        coVerify(exactly = 0) { solicitudes.crear(any()) }
    }

    @Test
    fun monto_aceptaSoloDigitos_yElTipoSinMontoLoDescarta() {
        viewModel.onTipoChange(TipoColaboracion.DONACION)
        viewModel.onMontoChange("$50.000abc")
        assertEquals("50000", viewModel.uiState.value.monto)

        viewModel.onTipoChange(TipoColaboracion.VOLUNTARIADO)
        assertEquals("", viewModel.uiState.value.monto)
        assertNull(viewModel.uiState.value.montoError)
    }

    @Test
    fun cambiarAUnTipoConMonto_revalidaElMontoEscrito() {
        viewModel.onTipoChange(TipoColaboracion.COMPRA_PRODUCTOS)
        viewModel.onMontoChange("500")
        viewModel.onTipoChange(TipoColaboracion.DONACION)
        assertNotNull(viewModel.uiState.value.montoError)
    }

    @Test
    fun enviarValido_creaLaSolicitudEnElMicroservicio() {
        coEvery { solicitudes.crear(any()) } answers { firstArg<Solicitud>().copy(id = 9) }
        llenarDonacion()

        viewModel.enviar()

        assertTrue(viewModel.uiState.value.enviada)
        coVerify { solicitudes.crear(match { it.usuarioId == 3L && it.monto == 50_000 && it.mensaje == "Queremos apoyar los talleres" }) }
    }

    @Test
    fun siElMicroservicioRechazaUnCampo_elErrorApareceBajoEseCampo() {
        coEvery { solicitudes.crear(any()) } throws ErrorRemoto("Revisa los datos", mapOf("fechaContacto" to "Elige otra fecha"))
        llenarDonacion()

        viewModel.enviar()

        val estado = viewModel.uiState.value
        assertFalse(estado.enviada)
        assertFalse(estado.enviando)
        assertEquals("Revisa los datos", estado.errorGeneral)
        assertEquals("Elige otra fecha", estado.fechaError)
    }

    @Test
    fun conLaSesionExpirada_muestraUnAviso() {
        coEvery { usuarios.idSesionActual() } returns null
        llenarDonacion()

        viewModel.enviar()

        assertNotNull(viewModel.uiState.value.errorGeneral)
        coVerify(exactly = 0) { solicitudes.crear(any()) }
    }

    @Test
    fun editar_cargaLosDatos_yGuardaConActualizar() {
        val guardada = Solicitud(5, 3, TipoColaboracion.DONACION, 20_000, manana, "Mensaje original de prueba")
        coEvery { solicitudes.buscar(5) } returns guardada
        coEvery { solicitudes.actualizar(any()) } answers { firstArg() }

        viewModel.cargar(5)
        assertEquals(5L, viewModel.uiState.value.editandoId)
        assertEquals("20000", viewModel.uiState.value.monto)
        // Se conservan los indicadores ya cargados
        assertEquals(indicadores, viewModel.uiState.value.indicadores)

        viewModel.onMensajeChange("Mensaje editado de prueba")
        viewModel.enviar()

        coVerify { solicitudes.actualizar(match { it.id == 5L && it.mensaje == "Mensaje editado de prueba" }) }
        coVerify(exactly = 0) { solicitudes.crear(any()) }
    }

    @Test
    fun cargarUnaSolicitudQueNoExiste_dejaElFormularioVacio() {
        coEvery { solicitudes.buscar(99) } returns null
        viewModel.cargar(99)
        assertNull(viewModel.uiState.value.editandoId)
    }
}
