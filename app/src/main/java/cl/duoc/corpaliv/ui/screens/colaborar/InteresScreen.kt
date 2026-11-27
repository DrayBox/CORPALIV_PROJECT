package cl.duoc.corpaliv.ui.screens.colaborar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.data.model.TipoColaboracion
import cl.duoc.corpaliv.domain.conversion.equivalencia
import cl.duoc.corpaliv.domain.validation.Validators
import cl.duoc.corpaliv.ui.components.AparecerVertical
import cl.duoc.corpaliv.ui.components.CorpalivTextField
import cl.duoc.corpaliv.ui.components.DateField
import cl.duoc.corpaliv.ui.components.ErrorBanner
import cl.duoc.corpaliv.ui.components.FieldErrorText
import cl.duoc.corpaliv.ui.components.PermisoRedLocal
import cl.duoc.corpaliv.ui.components.shakeOnError
import cl.duoc.corpaliv.ui.util.formatoDecimal
import cl.duoc.corpaliv.ui.util.formatoFecha
import cl.duoc.corpaliv.ui.util.formatoMonto
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.InteresViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InteresScreen(
    onBack: () -> Unit,
    onEnviada: () -> Unit,
    // Tipo ya elegido en Colaborar (null si se entra desde Mis solicitudes)
    tipoInicial: TipoColaboracion? = null,
    // Id de la solicitud a editar (null = solicitud nueva)
    solicitudId: Long? = null,
    viewModel: InteresViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(solicitudId) {
        if (solicitudId != null) viewModel.cargar(solicitudId)
    }

    LaunchedEffect(tipoInicial) {
        if (tipoInicial != null && state.tipo == null) viewModel.onTipoChange(tipoInicial)
    }

    LaunchedEffect(state.enviada) {
        if (state.enviada) onEnviada()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (state.editandoId == null) "Solicitud de interés" else "Editar solicitud") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            windowInsets = WindowInsets(0.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Cuéntanos cómo quieres aportar y el equipo de vinculación te contactará.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("¿Cómo quieres colaborar?", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.shakeOnError(state.intentoFallido, state.tipoError != null)
            ) {
                TipoColaboracion.entries.forEach { tipo ->
                    val seleccionado = state.tipo == tipo
                    FilterChip(
                        selected = seleccionado,
                        onClick = { viewModel.onTipoChange(tipo) },
                        label = { Text(tipo.etiqueta) },
                        leadingIcon = if (seleccionado) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else {
                            null
                        }
                    )
                }
            }
            FieldErrorText(state.tipoError)

            // El monto solo aparece (animado) para los tipos que lo usan
            AparecerVertical(visible = state.tipo?.usaMonto == true) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val obligatorio = state.tipo?.montoObligatorio != false
                CorpalivTextField(
                    value = state.monto,
                    onValueChange = viewModel::onMontoChange,
                    label = if (obligatorio) "Monto a donar (CLP)" else "Presupuesto estimado (opcional)",
                    leadingIcon = Icons.Outlined.Payments,
                    error = state.montoError,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.shakeOnError(state.intentoFallido, state.montoError != null),
                    supportingHint = state.monto.toIntOrNull()?.let { monto ->
                        // Con los indicadores de mindicador.cl se muestra cuánto es en UF y dólares
                        val equivalente = state.indicadores?.let { indicadores ->
                            val eq = equivalencia(monto, indicadores)
                            " ≈ ${formatoDecimal(eq.uf)} UF · US$ ${formatoDecimal(eq.dolares)}"
                        }.orEmpty()
                        formatoMonto(monto) + equivalente
                    } ?: "Entre ${formatoMonto(Validators.MONTO_MIN)} y ${formatoMonto(Validators.MONTO_MAX)}"
                )
                state.indicadores?.let {
                    Text(
                        "UF y dólar del ${formatoFecha(it.fecha)} · fuente: mindicador.cl",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
              }
            }

            DateField(
                value = state.fechaContacto,
                onValueChange = viewModel::onFechaChange,
                label = "¿Cuándo te contactamos?",
                error = state.fechaError,
                soloFuturas = true,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.fechaError != null)
            )

            CorpalivTextField(
                value = state.mensaje,
                onValueChange = viewModel::onMensajeChange,
                label = "Mensaje",
                leadingIcon = Icons.Outlined.ChatBubbleOutline,
                error = state.mensajeError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.mensajeError != null),
                imeAction = ImeAction.Default,
                singleLine = false,
                minLines = 4,
                supportingHint = "${state.mensaje.trim().length}/${Validators.MENSAJE_MAX}"
            )

            PermisoRedLocal()
            ErrorBanner(state.errorGeneral)

            Button(
                onClick = viewModel::enviar,
                enabled = !state.enviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
                    .height(52.dp)
            ) {
                if (state.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Text(if (state.editandoId == null) "Enviar solicitud" else "Guardar cambios", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
