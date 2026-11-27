plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.corpaliv"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.corpaliv"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Microservicios corriendo en el computador. 10.0.2.2 es el computador visto desde el emulador.
        buildConfigField("String", "URL_CONTENIDO", "\"http://10.0.2.2:8081/\"")
        buildConfigField("String", "URL_SOLICITUDES", "\"http://10.0.2.2:8082/\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
// Cobertura de las pruebas unitarias: ./gradlew koverHtmlReportDebug
// Se mide sobre la lógica (ViewModels, repositorios, dominio y capa remota), no sobre la interfaz.
kover {
    reports {
        filters {
            includes {
                packages(
                    "cl.duoc.corpaliv.viewmodel",
                    "cl.duoc.corpaliv.data.repository",
                    "cl.duoc.corpaliv.data.remote",
                    "cl.duoc.corpaliv.domain"
                )
            }
            excludes {
                // Fábrica de ViewModels y armado de Retrofit: solo conectan piezas, sin lógica propia
                classes("cl.duoc.corpaliv.viewmodel.AppViewModelProvider*", "cl.duoc.corpaliv.data.remote.RetrofitClient*")
            }
        }
        variant("debug") {
            verify {
                rule { minBound(80) }
            }
        }
    }
}
