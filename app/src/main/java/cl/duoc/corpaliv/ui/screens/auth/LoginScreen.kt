package cl.duoc.corpaliv.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.ui.components.CorpalivTextField
import cl.duoc.corpaliv.ui.components.ErrorBanner
import cl.duoc.corpaliv.ui.components.PasswordTextField
import cl.duoc.corpaliv.ui.components.shakeOnError
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegistro: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // La navegación ocurre una vez, cuando el ViewModel marca éxito
    LaunchedEffect(state.exito) {
        if (state.exito) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CORPALIV", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            "Inicia sesión para colaborar y seguir tus solicitudes",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))

        CorpalivTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo",
            leadingIcon = Icons.Outlined.Email,
            error = state.emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.shakeOnError(state.intentoFallido, state.emailError != null || state.errorGeneral != null)
        )
        PasswordTextField(
            value = state.clave,
            onValueChange = viewModel::onClaveChange,
            label = "Clave",
            error = state.claveError,
            imeAction = ImeAction.Done,
            modifier = Modifier.shakeOnError(state.intentoFallido, state.claveError != null || state.errorGeneral != null)
        )

        ErrorBanner(state.errorGeneral)

        Button(
            onClick = viewModel::iniciarSesion,
            enabled = !state.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Entrar")
            }
        }
        TextButton(onClick = onGoToRegistro) { Text("¿No tienes cuenta? Crear cuenta") }
    }
}
