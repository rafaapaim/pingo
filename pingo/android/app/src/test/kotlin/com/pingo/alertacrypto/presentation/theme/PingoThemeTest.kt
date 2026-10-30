package com.pingo.alertacrypto.presentation.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PingoThemeTest {
    @Test
    fun lightAndDarkThemesUseDistinctBackgrounds() {
        assertNotEquals(
            pingoColorScheme(darkTheme = false).background,
            pingoColorScheme(darkTheme = true).background,
        )
    }

    @Test
    fun bothThemesUseContrastingForegroundAndBackgroundColors() {
        assertTrue(hasReadableOnBackgroundContrast(darkTheme = false))
        assertTrue(hasReadableOnBackgroundContrast(darkTheme = true))
    }

    @Test
    fun allSpecifiedTextAndContainerPairsMeetWcagContrast() {
        listOf(false, true).forEach { darkTheme ->
            allPingoTextContrastRatios(darkTheme).forEach { (pair, ratio) ->
                assertTrue(
                    "${if (darkTheme) "dark" else "light"} $pair contrast was $ratio",
                    ratio >= 4.5f,
                )
            }
        }
    }

    @Test
    fun unspecifiedOrInvalidThemePreferenceDefaultsToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue("unknown"))
    }

    @Test
    fun systemThemeFollowsTheDeviceAndExplicitModesOverrideIt() {
        assertTrue(ThemeMode.SYSTEM.resolvesDarkTheme(systemDarkTheme = true))
        assertEquals(false, ThemeMode.SYSTEM.resolvesDarkTheme(systemDarkTheme = false))
        assertTrue(ThemeMode.DARK.resolvesDarkTheme(systemDarkTheme = false))
        assertEquals(false, ThemeMode.LIGHT.resolvesDarkTheme(systemDarkTheme = true))
    }
}
