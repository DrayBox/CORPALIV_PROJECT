package cl.duoc.corpaliv.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.duoc.corpaliv.ui.screens.auth.LoginScreen
import cl.duoc.corpaliv.ui.screens.auth.RegistroScreen
import cl.duoc.corpaliv.ui.screens.catalogo.CatalogoScreen
import cl.duoc.corpaliv.ui.screens.colaborar.ColaborarScreen
import cl.duoc.corpaliv.ui.screens.colaborar.InteresScreen
import cl.duoc.corpaliv.ui.screens.inicio.InicioScreen
import cl.duoc.corpaliv.ui.screens.perfil.PerfilScreen
import cl.duoc.corpaliv.ui.screens.perfil.SolicitudesScreen
import cl.duoc.corpaliv.ui.screens.programas.ProgramasScreen
import cl.duoc.corpaliv.ui.screens.sedes.SedesScreen
import cl.duoc.corpaliv.ui.components.Duracion
import cl.duoc.corpaliv.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
        // Por defecto, "fade through" de Material: la pantalla anterior se desvanece
        // y la nueva aparece con un leve zoom. Se usa entre secciones.
        enterTransition = { fadeIn(tween(Duracion.MEDIA, delayMillis = 90)) + scaleIn(tween(Duracion.MEDIA, delayMillis = 90), initialScale = 0.96f) },
        exitTransition = { fadeOut(tween(90)) },
        popEnterTransition = { fadeIn(tween(Duracion.MEDIA, delayMillis = 90)) + scaleIn(tween(Duracion.MEDIA, delayMillis = 90), initialScale = 0.96f) },
        popExitTransition = { fadeOut(tween(90)) }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = { destino ->
                    // Se saca el splash del historial: "atrás" no vuelve a él
                    navController.navigate(destino) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.INICIO) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegistro = { navController.navigate(Routes.REGISTRO) }
            )
        }
        pantallaSecundaria(Routes.REGISTRO) {
            RegistroScreen(
                onBack = { navController.popBackStack() },
                onRegistered = {
                    // El registro deja la sesión iniciada: se limpia login y registro del historial
                    navController.navigate(Routes.INICIO) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.INICIO) {
            InicioScreen(
                onGoToColaborar = { navController.navigateToSection(Routes.COLABORAR) },
                onGoToSedes = { navController.navigate(Routes.SEDES) }
            )
        }
        composable(Routes.PROGRAMAS) { ProgramasScreen() }
        composable(Routes.CATALOGO) { CatalogoScreen() }
        composable(Routes.COLABORAR) {
            ColaborarScreen(onGoToInteres = { navController.navigate(Routes.INTERES) })
        }
        composable(Routes.PERFIL) {
            PerfilScreen(
                onGoToSolicitudes = { navController.navigate(Routes.SOLICITUDES) },
                onLogout = {
                    // Limpia todo el historial y vuelve al login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        pantallaSecundaria(Routes.INTERES) {
            InteresScreen(
                onBack = { navController.popBackStack() },
                onEnviada = {
                    // Se cierra el formulario y se muestra la lista. Si se llegó
                    // desde Mis solicitudes, singleTop evita duplicarla.
                    navController.popBackStack()
                    navController.navigate(Routes.SOLICITUDES) { launchSingleTop = true }
                }
            )
        }
        pantallaSecundaria(Routes.SOLICITUDES) {
            SolicitudesScreen(
                onBack = { navController.popBackStack() },
                onNuevaSolicitud = { navController.navigate(Routes.INTERES) }
            )
        }
        pantallaSecundaria(Routes.SEDES) { SedesScreen(onBack = { navController.popBackStack() }) }
    }
}

// Navegación entre secciones de la barra inferior: no apila copias de la misma
// pantalla y guarda/restaura el estado de cada sección (por ejemplo, el scroll).
fun NavHostController.navigateToSection(route: String) {
    navigate(route) {
        popUpTo(Routes.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Pantallas secundarias (se abren "encima" de otra): entran desde la derecha
// y al volver salen hacia la derecha, para que se entienda la jerarquía.
private fun NavGraphBuilder.pantallaSecundaria(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    val deslizar = tween<IntOffset>(Duracion.MEDIA)
    composable(
        route = route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, deslizar) },
        exitTransition = { fadeOut(tween(Duracion.CORTA)) },
        popEnterTransition = { fadeIn(tween(Duracion.MEDIA)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, deslizar) }
    ) { entry -> content(entry) }
}
