package com.pingo.alertacrypto.widget

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshnessPolicy
import com.pingo.alertacrypto.domain.monitoring.QuoteSourceKind
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.format.PriceFormatter

val PriceWidgetStateDefinition = PreferencesGlanceStateDefinition
val PriceWidgetSymbolKey = stringPreferencesKey("price_widget_symbol")
const val DEFAULT_WIDGET_SYMBOL = "BTC"

data class PriceWidgetState(
    val symbol: String,
    val priceText: String,
    val changeText: String,
    val updatedAtText: String,
    val statusText: String,
    val freshness: QuoteFreshness,
    val showPrice: Boolean,
)

object PriceWidgetStateMapper {
    fun map(
        tick: PriceTick?,
        receivedAtMillis: Long?,
        nowMillis: Long,
        selectedSymbol: String = tick?.symbol ?: DEFAULT_WIDGET_SYMBOL,
    ): PriceWidgetState {
        val freshness = QuoteFreshnessPolicy.classify(
            sourceKind = QuoteSourceKind.WIDGET,
            receivedAtMillis = receivedAtMillis,
            nowMillis = nowMillis,
        )
        return when {
            tick == null || freshness == QuoteFreshness.MISSING -> PriceWidgetState(
                symbol = selectedSymbol,
                priceText = "--",
                changeText = "",
                updatedAtText = "",
                statusText = "Cotação indisponível",
                freshness = QuoteFreshness.MISSING,
                showPrice = false,
            )
            freshness == QuoteFreshness.STALE -> PriceWidgetState(
                symbol = selectedSymbol,
                priceText = PriceFormatter.brl(tick.priceBRL).replace('\u00a0', ' '),
                changeText = tick.change24hPercent?.let(PriceFormatter::percent).orEmpty(),
                updatedAtText = PriceFormatter.time(tick.observedAt),
                statusText = "Cotação desatualizada",
                freshness = QuoteFreshness.STALE,
                showPrice = false,
            )
            else -> PriceWidgetState(
                symbol = selectedSymbol,
                priceText = PriceFormatter.brl(tick.priceBRL).replace('\u00a0', ' '),
                changeText = tick.change24hPercent?.let(PriceFormatter::percent).orEmpty(),
                updatedAtText = PriceFormatter.time(tick.observedAt),
                statusText = "",
                freshness = QuoteFreshness.FRESH,
                showPrice = true,
            )
        }
    }
}
