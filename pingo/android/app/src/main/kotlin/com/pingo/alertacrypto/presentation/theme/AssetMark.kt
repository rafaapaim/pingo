package com.pingo.alertacrypto.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.price.Asset

@Composable
fun AssetMark(
    asset: Asset,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    Box(
        modifier = modifier.size(size)
            .clearAndSetSemantics {
                contentDescription = "${asset.displayName} (${asset.symbol})"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (asset == Asset.XRP) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
            )
        }
        Image(
            painter = painterResource(assetLogoResource(asset)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

internal fun assetLogoResource(asset: Asset): Int =
    when (asset) {
        Asset.BTC -> R.drawable.coin_btc
        Asset.ETH -> R.drawable.coin_eth
        Asset.SOL -> R.drawable.coin_sol
        Asset.XRP -> R.drawable.coin_xrp
        Asset.ADA -> R.drawable.coin_ada
        Asset.DOGE -> R.drawable.coin_doge
    }
