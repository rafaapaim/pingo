package com.pingo.alertacrypto.presentation.theme

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesStoreTest {
    @Test
    fun favoritesAndThemeSurviveStoreRecreation() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val firstStore = ThemePreferenceStore(context)
        try {
            firstStore.setFavorite("BTC", true)
            firstStore.setFavorite("not-a-coin", true)
            firstStore.setThemeMode(ThemeMode.DARK)

            val recreatedStore = ThemePreferenceStore(context)
            assertEquals(setOf("BTC"), recreatedStore.favoriteSymbols.first())
            assertEquals(ThemeMode.DARK, recreatedStore.themeMode.first())
        } finally {
            firstStore.setFavorite("BTC", false)
            firstStore.setThemeMode(ThemeMode.SYSTEM)
        }
    }
}
