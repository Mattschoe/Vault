package org.creategoodthings.vault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyDeep,
    onPrimary = Color.White,
    primaryContainer = NavyLight,
    onPrimaryContainer = NavyDark,

    secondary = RustRed,
    onSecondary = Color.White,
    secondaryContainer = RustPale,
    onSecondaryContainer = RustDark,

    tertiary = OliveGreen,
    onTertiary = Color.White,
    tertiaryContainer = OlivePale,
    onTertiaryContainer = OliveDark,

    background = BeigeBackground,
    onBackground = TextBlack,
    surface = BeigeSurface,
    onSurface = TextBlack,

    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = NeutralGrey,
    outline = OutlineGrey,

    error = ErrorRed,
    onError = ErrorWhite
)

@Composable
fun VaultTheme(content: @Composable () -> Unit) {
    //TODO Tilføj Dark mode check

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}