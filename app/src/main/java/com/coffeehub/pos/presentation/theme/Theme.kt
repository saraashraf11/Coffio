package com.coffeehub.pos.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


val DarkColorScheme = darkColorScheme(
    primary = EspressoBrown,
    onPrimary = WarmWhite,
    primaryContainer = EspressoBrownDark,
    onPrimaryContainer = CreamWhite,
    secondary = LatteCaramel,
    onSecondary = DarkRoast,
    secondaryContainer = LatteCaramelDark,
    onSecondaryContainer = CreamWhite,
    tertiary = CreamWhite,
    onTertiary = DarkRoast,
    background = DarkRoast,
    onBackground = WarmWhite,
    surface = RichBrownSurface,
    onSurface = WarmWhite,
    surfaceVariant = CardSurface,
    onSurfaceVariant = WarmGray,
    error = ErrorRed,
    onError = WarmWhite,
    outline = WarmGray
)

val LightColorScheme = lightColorScheme(
    primary = EspressoBrown,
    onPrimary = WarmWhite,
    primaryContainer = LatteCaramelLight,
    onPrimaryContainer = EspressoBrownDark,
    secondary = LatteCaramel,
    onSecondary = DarkRoast,
    secondaryContainer = LightCardSurface,
    onSecondaryContainer = EspressoBrownDark,
    tertiary = EspressoBrownLight,
    onTertiary = WarmWhite,
    background = LightCream,
    onBackground = DarkText,
    surface = LightSurface,
    onSurface = DarkText,
    surfaceVariant = LightCardSurface,
    onSurfaceVariant = EspressoBrownDark,
    error = ErrorRed,
    onError = WarmWhite,
    outline = LatteCaramel
)

@Composable
fun BrewPointTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BrewPointTypography,
        shapes = BrewPointShapes,
        content = content
    )
}
