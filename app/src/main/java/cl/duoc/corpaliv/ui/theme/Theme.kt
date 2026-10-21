package cl.duoc.corpaliv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema fijo con los colores de la marca. No usamos dynamic color
// (Android 12+) porque reemplazaría la paleta por la del fondo de pantalla.
private val CorpalivColorScheme = lightColorScheme(
    primary = CorpalivViolet,
    onPrimary = Color.White,
    primaryContainer = CorpalivVioletContainer,
    onPrimaryContainer = OnCorpalivVioletContainer,
    secondary = CorpalivMagenta,
    onSecondary = Color.White,
    secondaryContainer = CorpalivMagentaContainer,
    onSecondaryContainer = OnCorpalivMagentaContainer,
    tertiary = CorpalivYellow,
    onTertiary = CorpalivText,
    tertiaryContainer = CorpalivYellowContainer,
    onTertiaryContainer = CorpalivText,
    background = CorpalivBackground,
    onBackground = CorpalivText,
    surface = CorpalivBackground,
    onSurface = CorpalivText,
    surfaceVariant = CorpalivSurfaceVariant,
    onSurfaceVariant = CorpalivTextSecondary,
    outline = CorpalivOutline,
    outlineVariant = CorpalivOutlineVariant,
    error = CorpalivError,
    onError = Color.White,
    errorContainer = CorpalivErrorContainer,
    onErrorContainer = OnCorpalivErrorContainer
)

@Composable
fun CorpalivTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CorpalivColorScheme,
        typography = Typography,
        shapes = CorpalivShapes,
        content = content
    )
}
