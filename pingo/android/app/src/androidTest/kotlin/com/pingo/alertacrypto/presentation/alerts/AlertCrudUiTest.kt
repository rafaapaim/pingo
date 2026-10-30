package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
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
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertCrudUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createsAlertFromBrazilianCurrencyInput() {
        val saved = AtomicReference<AlertDraft?>()
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                AlertEditorScreen(
                    initialAlert = null,
                    onCancel = {},
                    onSave = { symbol, target, direction, repeatMode ->
                        saved.set(AlertDraft(symbol, target, direction, repeatMode))
                    },
                )
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("1.234,56")
        composeRule.onNodeWithText("Ambas").performClick()
        composeRule.onNodeWithText("Repetir").performClick()
        composeRule.onNodeWithText("Salvar").performClick()

        val draft = requireNotNull(saved.get())
        assertEquals("BTC", draft.symbol)
        assertEquals(BigDecimal("1234.56"), draft.target)
        assertEquals(AlertDirection.BOTH, draft.direction)
        assertEquals(RepeatMode.ONE_SHOT, draft.repeatMode)
    }

    @Test
    fun rejectsInvalidTargetWithPortugueseError() {
        val saved = AtomicReference(false)
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                AlertEditorScreen(
                    initialAlert = null,
                    onCancel = {},
                    onSave = { _, _, _, _ -> saved.set(true) },
                )
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("0")
        composeRule.onNodeWithText("Salvar").performClick()

        composeRule.onNodeWithText("Informe um valor-alvo positivo.").assertExists()
        assertFalse(saved.get())
    }

    private data class AlertDraft(
        val symbol: String,
        val target: BigDecimal,
        val direction: AlertDirection,
        val repeatMode: RepeatMode,
    )
}
