package cl.duoc.corpaliv.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

// Obtiene la ubicación aproximada con el LocationManager de Android (sin Google Play Services).
// Se llama solo después de que la UI confirmó el permiso ACCESS_COARSE_LOCATION.
private const val ESPERA_MAXIMA_MS = 10_000L

class LocationProvider(context: Context) {

    private val appContext = context.applicationContext
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun ubicacionActivada(): Boolean = LocationManagerCompat.isLocationEnabled(locationManager)

    // null si no se pudo obtener (por ejemplo, ubicación apagada).
    // Espera como máximo 10 s una ubicación nueva; si no llega, usa la última conocida.
    suspend fun ubicacionActual(): Location? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return ultimaConocida() // Android 8 a 10
        val nueva = withTimeoutOrNull(ESPERA_MAXIMA_MS) {
            proveedoresDisponibles().firstNotNullOfOrNull { proveedor ->
                // En Android 8 a 11 el GPS exige ubicación precisa: ese proveedor se salta
                try { pedirUbicacion(proveedor) } catch (e: SecurityException) { null }
            }
        }
        return nueva ?: ultimaConocida()
    }

    @SuppressLint("MissingPermission") // el permiso se verifica en la UI antes de llamar
    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun pedirUbicacion(proveedor: String): Location? = suspendCancellableCoroutine { cont ->
        val cancelar = CancellationSignal()
        cont.invokeOnCancellation { cancelar.cancel() }
        locationManager.getCurrentLocation(proveedor, cancelar, ContextCompat.getMainExecutor(appContext)) { ubicacion ->
            cont.resume(ubicacion)
        }
    }

    // Red primero (rápida en interiores), luego GPS y al final fused,
    // que con ubicación aproximada depende de la red y puede tardar mucho
    private fun proveedoresDisponibles(): List<String> = buildList {
        add(LocationManager.NETWORK_PROVIDER)
        add(LocationManager.GPS_PROVIDER)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
    }.filter { locationManager.isProviderEnabled(it) }

    @SuppressLint("MissingPermission")
    private fun ultimaConocida(): Location? =
        locationManager.getProviders(true)
            .mapNotNull { runCatching { locationManager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
}
