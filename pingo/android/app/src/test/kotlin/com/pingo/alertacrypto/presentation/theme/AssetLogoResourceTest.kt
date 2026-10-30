package com.pingo.alertacrypto.presentation.theme

import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.price.Asset
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetLogoResourceTest {
    @Test
    fun mapsEverySupportedAssetToItsLocalLogo() {
        assertEquals(
            listOf(
                R.drawable.coin_btc,
                R.drawable.coin_eth,
                R.drawable.coin_sol,
                R.drawable.coin_xrp,
                R.drawable.coin_ada,
                R.drawable.coin_doge,
            ),
            Asset.supported.map(::assetLogoResource),
        )
        assertEquals(Asset.supported.size, Asset.supported.map(::assetLogoResource).distinct().size)
    }
}
