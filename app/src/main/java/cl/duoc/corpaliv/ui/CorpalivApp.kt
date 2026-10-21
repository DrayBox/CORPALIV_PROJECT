package cl.duoc.corpaliv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.corpaliv.navigation.AppNavHost
import cl.duoc.corpaliv.navigation.bottomNavItems
import cl.duoc.corpaliv.navigation.navigateToSection
import cl.duoc.corpaliv.ui.components.CorpalivBottomBar
import cl.duoc.corpaliv.ui.components.Duracion

// Raíz de la interfaz: Scaffold con la barra inferior + el NavHost
@Composable
fun CorpalivApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // La barra solo se muestra en las 5 secciones principales
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // La barra entra y sale deslizándose desde abajo
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(Duracion.MEDIA)) { it } + fadeIn(tween(Duracion.MEDIA)),
                exit = slideOutVertically(tween(Duracion.MEDIA)) { it } + fadeOut(tween(Duracion.CORTA))
            ) {
                CorpalivBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> navController.navigateToSection(route) }
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
