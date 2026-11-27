package cl.duoc.corpaliv.data.remote

import cl.duoc.corpaliv.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Arma un Retrofit para una URL base. Se usa una vez por servicio desde AppContainer.
object RetrofitClient {

    // En debug, Logcat muestra cada petición y respuesta (filtro: okhttp.OkHttpClient)
    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }

    fun crear(urlBase: String): Retrofit = Retrofit.Builder()
        .baseUrl(urlBase)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
