package com.pingo.alertacrypto.data.notification

import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import java.util.Locale

internal object AlertNotificationTextFormatter {
    fun format(
        event: AlertNotificationEvent,
        upTemplate: String,
        downTemplate: String,
    ): String = String.format(
        Locale.forLanguageTag("pt-BR"),
        if (event.directionUp) upTemplate else downTemplate,
        event.symbol,
        PriceFormatter.brl(event.targetPriceBRL).replace('\u00a0', ' '),
        PriceFormatter.brl(event.observedPriceBRL).replace('\u00a0', ' '),
    )
}
