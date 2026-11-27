package cl.duoc.corpaliv.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

// Una lista de Compose mantiene fijo el primer elemento visible: si llega uno nuevo
// arriba (por ejemplo, agregado en la base de datos), queda fuera de la vista.
// Si el usuario estaba mirando el inicio, se vuelve arriba para que se vea al tiro.
@Composable
fun MostrarNuevosArriba(listState: LazyListState, primerId: Any?) {
    LaunchedEffect(primerId) {
        if (primerId != null && listState.firstVisibleItemIndex <= 1) listState.animateScrollToItem(0)
    }
}
