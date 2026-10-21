package cl.duoc.corpaliv.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.corpaliv.ui.components.Duracion
import cl.duoc.corpaliv.viewmodel.AppViewModelProvider
import cl.duoc.corpaliv.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onFinished: (destino: String) -> Unit,
    viewModel: SplashViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val destino by viewModel.destino.collectAsStateWithLifecycle()

    // Logo: aparece creciendo de 80% a 100%. Lema: aparece después.
    val escalaLogo = remember { Animatable(0.8f) }
    val alfaLogo = remember { Animatable(0f) }
    val alfaLema = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { escalaLogo.animateTo(1f, tween(Duracion.LARGA, easing = FastOutSlowInEasing)) }
        launch { alfaLogo.animateTo(1f, tween(Duracion.LARGA)) }
        launch { alfaLema.animateTo(1f, tween(Duracion.MEDIA, delayMillis = Duracion.LARGA - 200)) }
    }

    LaunchedEffect(destino) {
        destino?.let(onFinished)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CORPALIV",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.graphicsLayer {
                scaleX = escalaLogo.value
                scaleY = escalaLogo.value
                alpha = alfaLogo.value
            }
        )
        Text(
            "Comprometidos con la discapacidad múltiple",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.graphicsLayer { alpha = alfaLema.value }
        )
    }
}
