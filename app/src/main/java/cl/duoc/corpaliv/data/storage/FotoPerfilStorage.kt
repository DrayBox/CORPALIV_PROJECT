package cl.duoc.corpaliv.data.storage

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Guarda la foto de perfil en el almacenamiento interno de la app
// (files/fotos). Ninguna otra app puede leer esa carpeta.
class FotoPerfilStorage(context: Context) {

    private val carpeta = File(context.filesDir, "fotos")

    // Devuelve la ruta del archivo nuevo. El nombre lleva la hora para que
    // la UI note el cambio; la foto anterior se borra.
    suspend fun guardar(foto: Bitmap, rutaAnterior: String?): String = withContext(Dispatchers.IO) {
        carpeta.mkdirs()
        val archivo = File(carpeta, "perfil_${System.currentTimeMillis()}.jpg")
        archivo.outputStream().use { foto.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        rutaAnterior?.let { File(it).delete() }
        archivo.absolutePath
    }
}
