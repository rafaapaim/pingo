package com.pingo.alertacrypto.presentation.navigation

import android.content.Intent
import com.pingo.alertacrypto.domain.price.Asset
import java.net.URLEncoder
import java.net.URI
import java.nio.charset.StandardCharsets

object PingoRoutes {
    const val MARKET = "market"
    const val ALERTS = "alerts"
    const val SETTINGS = "settings"
    const val SYMBOL_ARGUMENT = "symbol"
    const val ALERT_ID_ARGUMENT = "alertId"
    const val ASSET_DETAILS = "asset/{$SYMBOL_ARGUMENT}"
    const val ALERT_EDITOR = "alert-editor?alertId={$ALERT_ID_ARGUMENT}&symbol={$SYMBOL_ARGUMENT}"
    const val ASSET_DEEP_LINK = "pingo://asset/{symbol}"

    fun assetDetails(symbol: String): String =
        "asset/${URLEncoder.encode(symbol, StandardCharsets.UTF_8).replace("+", "%20")}"

    fun alertEditor(alertId: String? = null, symbol: String? = null): String =
        "alert-editor?alertId=${encode(alertId.orEmpty())}&symbol=${encode(symbol.orEmpty())}"

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20")
}

object PriceDetailDeepLink {
    fun supportedSymbol(intent: Intent): String? {
        return resolve(intent.dataString, intent.getStringExtra(PriceDetailDestination.EXTRA_SYMBOL))
    }

    fun isDeepLinkIntent(intent: Intent): Boolean =
        intent.data != null || intent.hasExtra(PriceDetailDestination.EXTRA_SYMBOL)

    fun resolve(uriString: String?, extraSymbol: String? = null): String? {
        val candidate = if (uriString != null) {
            val uri = runCatching { URI(uriString) }.getOrNull() ?: return null
            if (uri.scheme != "pingo" || uri.host != "asset") return null
            val path = uri.path?.removePrefix("/") ?: return null
            if (path.isBlank() || path.contains('/')) return null
            path
        } else {
            extraSymbol
        } ?: return null
        return Asset.fromSymbol(candidate)?.symbol
    }
}
