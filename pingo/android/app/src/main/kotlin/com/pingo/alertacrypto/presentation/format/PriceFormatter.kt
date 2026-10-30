package com.pingo.alertacrypto.presentation.format

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object PriceFormatter {
    private val brazilianLocale = Locale.forLanguageTag("pt-BR")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", brazilianLocale)

    fun brl(value: BigDecimal): String =
        NumberFormat.getCurrencyInstance(brazilianLocale)
            .format(value)
            .replace('\u00a0', ' ')

    fun percent(value: BigDecimal): String =
        "${NumberFormat.getNumberInstance(brazilianLocale).format(value)}%"

    fun time(value: Instant): String =
        timeFormatter.withZone(ZoneId.systemDefault()).format(value)
}
