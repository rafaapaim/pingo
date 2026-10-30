package com.pingo.alertacrypto.presentation.navigation

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityDeepLinkTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun coldStartIntentResolvesSupportedAsset() {
        val context = composeRule.activity
        val intent = PriceDetailDestination.intent(context, "BTC")
        val scenario = ActivityScenario.launch<MainActivity>(intent)

        scenario.onActivity { activity ->
            assertEquals("BTC", PriceDetailDeepLink.supportedSymbol(activity.intent))
        }
        scenario.close()
    }

    @Test
    fun foregroundOnNewIntentNavigatesToRequestedAsset() {
        val intent = PriceDetailDestination.intent(composeRule.activity, "ETH")

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.startActivity(intent)
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Ethereum").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Ethereum").assertIsDisplayed()
    }

    @Test
    fun backgroundActivityNavigatesWhenReturnedWithNewIntent() {
        val scenario = composeRule.activityRule.scenario
        val intent = PriceDetailDestination.intent(composeRule.activity, "SOL")
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.onActivity { activity -> activity.startActivity(intent) }
        scenario.moveToState(Lifecycle.State.RESUMED)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Solana").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Solana").assertIsDisplayed()
    }

    @Test
    fun detailRouteSurvivesActivityRecreation() {
        val scenario = composeRule.activityRule.scenario
        val intent = PriceDetailDestination.intent(composeRule.activity, "ADA")
        scenario.onActivity { it.startActivity(intent) }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Cardano").fetchSemanticsNodes().isNotEmpty()
        }

        scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Cardano").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Cardano").assertIsDisplayed()
    }

    @Test
    fun unknownAssetReturnsToMarketWithoutCrashing() {
        val intent = Intent(composeRule.activity, MainActivity::class.java)
            .setData(android.net.Uri.parse("pingo://asset/UNKNOWN"))
            .putExtra(PriceDetailDestination.EXTRA_SYMBOL, "UNKNOWN")

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.startActivity(intent)
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Moedas").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Moedas").assertIsDisplayed()
    }
}
