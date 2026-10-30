package com.pingo.alertacrypto.presentation.theme

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.price.Asset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedComponentsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun chipTextAndCoinFallbackAreAccessibleInBothThemes() {
        listOf(false, true).forEach { darkTheme ->
            composeRule.setContent {
                PingoTheme(darkTheme) {
                    StatusChip("Ativo", StatusChipTone.POSITIVE)
                    AssetMark(Asset.BTC)
                }
            }
            composeRule.onNodeWithText("Ativo").assertExists()
            composeRule.onNodeWithContentDescription("Bitcoin (BTC)")
                .assertContentDescriptionEquals("Bitcoin (BTC)")
        }
    }
}
