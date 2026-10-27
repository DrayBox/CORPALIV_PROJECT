package cl.duoc.corpaliv.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import cl.duoc.corpaliv.data.model.TipoCuenta
import cl.duoc.corpaliv.domain.validation.Validators
import cl.duoc.corpaliv.ui.components.CheckboxField
import cl.duoc.corpaliv.ui.components.CorpalivTextField
import cl.duoc.corpaliv.ui.components.DateField
import cl.duoc.corpaliv.ui.components.PasswordTextField
import cl.duoc.corpaliv.ui.components.SwitchField
import cl.duoc.corpaliv.ui.components.shakeOnError
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: RegistroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.exito) {
        if (state.exito) onRegistered()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Crear cuenta") },
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
            Text("Tipo de cuenta", style = MaterialTheme.typography.titleSmall)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                TipoCuenta.entries.forEachIndexed { index, tipo ->
                    SegmentedButton(
                        selected = state.tipoCuenta == tipo,
                        onClick = { viewModel.onTipoCuentaChange(tipo) },
                        shape = SegmentedButtonDefaults.itemShape(index, TipoCuenta.entries.size)
                    ) { Text(tipo.etiqueta) }
                }
            }

            CorpalivTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = if (state.tipoCuenta == TipoCuenta.EMPRESA) "Nombre de contacto" else "Nombre completo",
                leadingIcon = Icons.Outlined.Person,
                error = state.nombreError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.nombreError != null)
            )
            CorpalivTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo",
                leadingIcon = Icons.Outlined.Email,
                error = state.emailError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.emailError != null),
                keyboardType = KeyboardType.Email
            )
            DateField(
                value = state.fechaNacimiento,
                onValueChange = viewModel::onFechaChange,
                label = "Fecha de nacimiento",
                error = state.fechaError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.fechaError != null),
                soloPasadas = true,
                // La edad se calcula sola y se muestra bajo la fecha
                supportingHint = state.edad?.let { "Tienes $it años" } ?: "Mínimo ${Validators.EDAD_MINIMA} años"
            )
            PasswordTextField(
                value = state.clave,
                onValueChange = viewModel::onClaveChange,
                label = "Clave",
                error = state.claveError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.claveError != null),
                supportingHint = "Mínimo ${Validators.CLAVE_MIN} caracteres, con letras y números"
            )
            PasswordTextField(
                value = state.confirmacion,
                onValueChange = viewModel::onConfirmacionChange,
                label = "Repetir clave",
                error = state.confirmacionError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.confirmacionError != null),
                imeAction = ImeAction.Done
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            SwitchField(
                checked = state.newsletter,
                onCheckedChange = viewModel::onNewsletterChange,
                label = "Recibir novedades",
                description = "Noticias de CORPALIV y Alba Lab por correo"
            )
            CheckboxField(
                checked = state.aceptaTerminos,
                onCheckedChange = viewModel::onTerminosChange,
                label = "Acepto los términos y la política de privacidad",
                error = state.terminosError,
                modifier = Modifier.shakeOnError(state.intentoFallido, state.terminosError != null)
            )

            Button(
                onClick = viewModel::registrar,
                enabled = !state.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
                    .height(52.dp)
            ) {
                if (state.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.Badge, contentDescription = null)
                    Text("Crear cuenta", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
