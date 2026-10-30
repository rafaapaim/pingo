package com.pingo.alertacrypto.presentation.navigation

import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.net.Uri
import com.pingo.alertacrypto.MainActivity
import com.pingo.alertacrypto.domain.price.Asset

object PriceDetailDestination {
    const val EXTRA_SYMBOL = "com.pingo.alertacrypto.extra.PRICE_SYMBOL"
    const val REQUEST_CODE_BASE = 0x504900

    fun uriString(symbol: String): String = "pingo://asset/$symbol"

    fun requestCode(symbol: String): Int =
        REQUEST_CODE_BASE + requireNotNull(Asset.fromSymbol(symbol)) {
            "Unsupported asset symbol: $symbol"
        }.ordinal

    fun intent(context: Context, symbol: String): Intent =
        Intent(context, MainActivity::class.java)
            .setData(Uri.parse(uriString(symbol)))
            .putExtra(EXTRA_SYMBOL, symbol)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

    fun pendingIntent(context: Context, symbol: String): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode(symbol),
            intent(context, symbol),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
