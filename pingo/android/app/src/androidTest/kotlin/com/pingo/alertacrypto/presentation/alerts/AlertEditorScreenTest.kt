package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertEditorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun brazilianDecimalAndBothDirectionAreSavedInSingleForm() {
        val saved = AtomicReference<Triple<BigDecimal, AlertDirection, RepeatMode>?>(null)
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                AlertEditorScreen(
                    initialAlert = null,
                    initialSymbol = "BTC",
                    onCancel = {},
                    onSave = { _, target, direction, repeat ->
                        saved.set(Triple(target, direction, repeat))
                    },
                )
            }
        }

        composeRule.onNodeWithText("Criar alerta").assertIsDisplayed()
        composeRule.onNodeWithTag("alert_target").performTextInput("1.234,56")
        composeRule.onNodeWithText("Ambas").performClick()
        composeRule.onNodeWithText("Salvar").performClick()
        composeRule.runOnIdle {
            assertEquals(
                Triple(
                    BigDecimal("1234.56"),
                    AlertDirection.BOTH,
                    RepeatMode.REPEATING,
                ),
                saved.get(),
            )
        }
    }
}
