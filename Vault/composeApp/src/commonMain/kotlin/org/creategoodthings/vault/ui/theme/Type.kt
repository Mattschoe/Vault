package org.creategoodthings.vault.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import vault.composeapp.generated.resources.Merriweather
import vault.composeapp.generated.resources.PlayfairDisplay
import vault.composeapp.generated.resources.Res

@Composable
fun appTypography(): Typography {
    val playfair = FontFamily(Font(Res.font.PlayfairDisplay))
    val merriweather = FontFamily(Font(Res.font.Merriweather))
    val baseline = Typography()
    return Typography(
        displayLarge   = baseline.displayLarge.copy(fontFamily = playfair),
        displayMedium  = baseline.displayMedium.copy(fontFamily = playfair),
        displaySmall   = baseline.displaySmall.copy(fontFamily = playfair),
        headlineLarge  = baseline.headlineLarge.copy(fontFamily = playfair),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = playfair),
        headlineSmall  = baseline.headlineSmall.copy(fontFamily = playfair),
        titleLarge     = baseline.titleLarge.copy(fontFamily = playfair),
        titleMedium    = baseline.titleMedium.copy(fontFamily = playfair),
        titleSmall     = baseline.titleSmall.copy(fontFamily = playfair),
        bodyLarge      = baseline.bodyLarge.copy(fontFamily = merriweather),
        bodyMedium     = baseline.bodyMedium.copy(fontFamily = merriweather),
        bodySmall      = baseline.bodySmall.copy(fontFamily = merriweather),
        labelLarge     = baseline.labelLarge.copy(fontFamily = merriweather),
        labelMedium    = baseline.labelMedium.copy(fontFamily = merriweather),
        labelSmall     = baseline.labelSmall.copy(fontFamily = merriweather),
    )
}