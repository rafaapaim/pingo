package com.pingo.alertacrypto.presentation.alerts

import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.CrossingDetector
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import java.math.BigDecimal

enum class AlertCardStatus {
    ACTIVE,
    PAUSED,
    WAITING_FOR_REARM,
    FIRED,
}

data class AlertCardUiModel(
    val alert: Alert,
    val status: AlertCardStatus,
    val currentPriceText: String?,
    val distanceText: String?,
    val reactivationText: String?,
    val quoteFreshness: QuoteFreshness?,
)

object AlertCardProjector {
    fun project(
        alert: Alert,
        tick: PriceTick?,
        quoteFreshness: QuoteFreshness? = null,
    ): AlertCardUiModel {
        val status = when {
            alert.repeatMode == RepeatMode.ONE_SHOT && !alert.armed -> AlertCardStatus.FIRED
            !alert.enabled -> AlertCardStatus.PAUSED
            !alert.armed && alert.repeatMode == RepeatMode.REPEATING ->
                AlertCardStatus.WAITING_FOR_REARM
            else -> AlertCardStatus.ACTIVE
        }
        val price = tick?.priceBRL?.takeIf { tick.symbol == alert.symbol }
        val delta = price?.subtract(alert.targetPriceBRL)?.abs()
        val reactivationText = when {
            status != AlertCardStatus.WAITING_FOR_REARM -> null
            alert.firedUp == true -> "Reativa quando o preço voltar abaixo de ${
                PriceFormatter.brl(rearmThreshold(alert.targetPriceBRL, firedUp = true))
            }"
            alert.firedUp == false -> "Reativa quando o preço voltar acima de ${
                PriceFormatter.brl(rearmThreshold(alert.targetPriceBRL, firedUp = false))
            }"
            else -> null
        }
        return AlertCardUiModel(
            alert = alert,
            status = status,
            currentPriceText = price?.let(PriceFormatter::brl),
            distanceText = delta?.let { "Distância: ${PriceFormatter.brl(it)}" },
            reactivationText = reactivationText,
            quoteFreshness = quoteFreshness,
        )
    }

    fun rearmThreshold(targetPriceBRL: BigDecimal, firedUp: Boolean): BigDecimal =
        targetPriceBRL.multiply(
            if (firedUp) BigDecimal.ONE.subtract(CrossingDetector.HYSTERESIS)
            else BigDecimal.ONE.add(CrossingDetector.HYSTERESIS),
        )
}
