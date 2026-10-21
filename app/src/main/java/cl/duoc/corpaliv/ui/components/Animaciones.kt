package cl.duoc.corpaliv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// Duraciones compartidas para que todas las animaciones se sientan iguales
object Duracion {
    const val CORTA = 150
    const val MEDIA = 300
    const val LARGA = 700
}

// Hace temblar el elemento cuando "intento" aumenta y hay error.
// Los ViewModels suben el contador cada vez que un envío falla la validación.
@Composable
fun Modifier.shakeOnError(intento: Int, hayError: Boolean): Modifier {
    val desplazamiento = remember { Animatable(0f) }
    val amplitud = with(LocalDensity.current) { 8.dp.toPx() }
    LaunchedEffect(intento) {
        if (intento > 0 && hayError) {
            // Izquierda-derecha cada vez más suave, y vuelve al centro
            for (factor in listOf(1f, -1f, 0.6f, -0.6f, 0.3f, 0f)) {
                desplazamiento.animateTo(amplitud * factor, tween(durationMillis = 50))
            }
        }
    }
    return this.graphicsLayer { translationX = desplazamiento.value }
}

// Muestra u oculta contenido con fundido + expansión vertical
@Composable
fun AparecerVertical(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(Duracion.MEDIA)) + expandVertically(tween(Duracion.MEDIA, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(Duracion.CORTA)) + shrinkVertically(tween(Duracion.MEDIA, easing = FastOutSlowInEasing))
    ) { content() }
}
