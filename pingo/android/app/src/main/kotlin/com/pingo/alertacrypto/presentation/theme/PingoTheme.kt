package com.pingo.alertacrypto.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PingoChipColors(
    val foreground: Color,
    val container: Color,
)

data class PingoColorTokens(
    val positive: Color,
    val negative: Color,
    val warning: Color,
    val neutral: Color,
    val positiveChip: PingoChipColors,
    val negativeChip: PingoChipColors,
    val warningChip: PingoChipColors,
    val neutralChip: PingoChipColors,
    val positiveVariation: Color,
    val negativeVariation: Color,
)

private val LightTokens = PingoColorTokens(
    positive = Color(0xFF16865B),
    negative = Color(0xFFD64D59),
    warning = Color(0xFFD69A31),
    neutral = Color(0xFF85818E),
    positiveChip = PingoChipColors(Color(0xFF0B5C3E), Color(0xFFDDF3E9)),
    negativeChip = PingoChipColors(Color(0xFF8F2330), Color(0xFFFBE3E5)),
    warningChip = PingoChipColors(Color(0xFF6B4700), Color(0xFFFBEBCB)),
    neutralChip = PingoChipColors(Color(0xFF4A4653), Color(0xFFECE9F0)),
    positiveVariation = Color(0xFF0F6B47),
    negativeVariation = Color(0xFFB3303D),
)

private val DarkTokens = PingoColorTokens(
    positive = Color(0xFF4CCB91),
    negative = Color(0xFFFF6B75),
    warning = Color(0xFFF2BE58),
    neutral = Color(0xFFAAA5B2),
    positiveChip = PingoChipColors(Color(0xFF8FE3BC), Color(0xFF12372A)),
    negativeChip = PingoChipColors(Color(0xFFFFB3B8), Color(0xFF4A1D22)),
    warningChip = PingoChipColors(Color(0xFFF2BE58), Color(0xFF4A3710)),
    neutralChip = PingoChipColors(Color(0xFFCFCAD8), Color(0xFF36323D)),
    positiveVariation = Color(0xFF4CCB91),
    negativeVariation = Color(0xFFFF6B75),
)

object PingoColors {
    fun tokens(darkTheme: Boolean): PingoColorTokens =
        if (darkTheme) DarkTokens else LightTokens
}

val LocalPingoColors = staticCompositionLocalOf { LightTokens }

@Composable
fun PingoTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = pingoColorScheme(darkTheme)
    val baseTypography = Typography()
    CompositionLocalProvider(LocalPingoColors provides PingoColors.tokens(darkTheme)) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = baseTypography.copy(
                displayLarge = baseTypography.displayLarge.copy(fontFamily = FontFamily.Default),
                headlineLarge = baseTypography.headlineLarge.copy(
                    fontFamily = FontFamily.Default,
                    fontSize = 32.sp,
                ),
                headlineMedium = baseTypography.headlineMedium.copy(
                    fontFamily = FontFamily.Default,
                    fontSize = 30.sp,
                ),
                titleLarge = baseTypography.titleLarge.copy(
                    fontFamily = FontFamily.Default,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                titleMedium = baseTypography.titleMedium.copy(fontFamily = FontFamily.Default),
                bodyLarge = baseTypography.bodyLarge.copy(fontFamily = FontFamily.Default),
                bodyMedium = baseTypography.bodyMedium.copy(fontFamily = FontFamily.Default),
                bodySmall = baseTypography.bodySmall.copy(
                    fontFamily = FontFamily.Default,
                    fontSize = 12.sp,
                ),
                labelLarge = baseTypography.labelLarge.copy(fontFamily = FontFamily.Default),
            ),
            shapes = Shapes(
                small = RoundedCornerShape(12.dp),
                medium = RoundedCornerShape(16.dp),
                large = RoundedCornerShape(16.dp),
            ),
            content = content,
        )
    }
}

internal fun pingoColorScheme(darkTheme: Boolean) =
    if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            background = Color(0xFF141218),
            onBackground = Color(0xFFE6E1E9),
            surface = Color(0xFF211F26),
            onSurface = Color(0xFFE6E1E9),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            outline = Color(0xFF938F99),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            background = Color(0xFFF7F5FB),
            onBackground = Color(0xFF171522),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF171522),
            surfaceVariant = Color(0xFFE9E4F2),
            onSurfaceVariant = Color(0xFF49454F),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            outline = Color(0xFF79747E),
        )
    }

internal fun hasReadableOnBackgroundContrast(darkTheme: Boolean): Boolean {
    val scheme = pingoColorScheme(darkTheme)
    return contrastRatio(scheme.onBackground, scheme.background) >= 4.5f
}

internal fun contrastRatio(first: Color, second: Color): Float {
    val lighter = maxOf(first.luminance(), second.luminance())
    val darker = minOf(first.luminance(), second.luminance())
    return (lighter + 0.05f) / (darker + 0.05f)
}

internal fun allPingoTextContrastRatios(darkTheme: Boolean): List<Pair<String, Float>> {
    val scheme = pingoColorScheme(darkTheme)
    val tokens = PingoColors.tokens(darkTheme)
    return buildList {
        listOf(
            "onSurface/background" to (scheme.onSurface to scheme.background),
            "onSurface/surface" to (scheme.onSurface to scheme.surface),
            "onSurface/surfaceVariant" to (scheme.onSurface to scheme.surfaceVariant),
            "primary/background" to (scheme.primary to scheme.background),
            "primary/surface" to (scheme.primary to scheme.surface),
            "positiveChip" to (tokens.positiveChip.foreground to tokens.positiveChip.container),
            "negativeChip" to (tokens.negativeChip.foreground to tokens.negativeChip.container),
            "warningChip" to (tokens.warningChip.foreground to tokens.warningChip.container),
            "neutralChip" to (tokens.neutralChip.foreground to tokens.neutralChip.container),
            "positiveVariation/background" to (tokens.positiveVariation to scheme.background),
            "positiveVariation/surface" to (tokens.positiveVariation to scheme.surface),
            "negativeVariation/background" to (tokens.negativeVariation to scheme.background),
            "negativeVariation/surface" to (tokens.negativeVariation to scheme.surface),
        ).forEach { (name, pair) ->
            add(name to contrastRatio(pair.first, pair.second))
        }
    }
}
