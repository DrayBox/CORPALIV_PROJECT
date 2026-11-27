package cl.duoc.corpaliv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

// Ejecuta accion al mostrar la pantalla y luego cada intervaloMs mientras sigue visible.
// Se detiene sola al salir de la pantalla o al pasar la app a segundo plano.
@Composable
fun ActualizarMientrasVisible(intervaloMs: Long = 5_000, accion: suspend () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val accionActual by rememberUpdatedState(accion)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                accionActual()
                delay(intervaloMs)
            }
        }
    }
}
