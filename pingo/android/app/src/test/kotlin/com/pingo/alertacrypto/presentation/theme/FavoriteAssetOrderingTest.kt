package com.pingo.alertacrypto.presentation.theme

import com.pingo.alertacrypto.domain.price.Asset
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteAssetOrderingTest {
    @Test
    fun ordersFavoritesFirstWithCanonicalOrderAndKeepsAllAssets() {
        val ordered = FavoriteAssetOrdering.order(setOf("DOGE", "BTC"))
        assertEquals(
            listOf(Asset.BTC, Asset.DOGE, Asset.ETH, Asset.SOL, Asset.XRP, Asset.ADA),
            ordered,
        )
    }

    @Test
    fun emptyAllAndUnknownFavoriteSetsKeepSupportedAssetsOnly() {
        assertEquals(Asset.supported, FavoriteAssetOrdering.order(emptySet()))
        assertEquals(Asset.supported, FavoriteAssetOrdering.order(setOf("unknown")))
        assertEquals(
            listOf(Asset.BTC, Asset.ETH, Asset.SOL, Asset.XRP, Asset.ADA, Asset.DOGE),
            FavoriteAssetOrdering.order(Asset.supported.map { it.symbol }.toSet()),
        )
    }
}
