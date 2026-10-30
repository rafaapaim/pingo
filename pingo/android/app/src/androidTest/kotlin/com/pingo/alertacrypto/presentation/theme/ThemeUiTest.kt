package com.pingo.alertacrypto.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun switchingThemeKeepsForegroundReadableAgainstSurface() {
        val luminance = AtomicReference<Pair<Float, Float>>()
        composeRule.setContent {
            val darkTheme = remember { mutableStateOf(false) }
            PingoTheme(darkTheme = darkTheme.value) {
                val colorScheme = MaterialTheme.colorScheme
                SideEffect {
                    luminance.set(
                        colorScheme.background.luminance() to
                            colorScheme.onBackground.luminance(),
                    )
                }
                TextButton(onClick = { darkTheme.value = !darkTheme.value }) {
                    Text("Alternar tema")
                }
                Text("Texto para validar contraste")
            }
        }

        composeRule.waitForIdle()
        assertReadableContrast(luminance.get())
        composeRule.onNodeWithText("Alternar tema").performClick()
        composeRule.waitForIdle()
        assertReadableContrast(luminance.get())
    }

    private fun assertReadableContrast(luminance: Pair<Float, Float>?) {
        requireNotNull(luminance)
        assertTrue(kotlin.math.abs(luminance.first - luminance.second) > 0.5f)
    }
}
