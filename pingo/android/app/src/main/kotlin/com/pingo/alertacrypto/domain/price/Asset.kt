package com.pingo.alertacrypto.domain.price

enum class Asset(
    val symbol: String,
    val displayName: String,
    val binanceSymbol: String,
    val coinGeckoId: String,
) {
    BTC("BTC", "Bitcoin", "BTCBRL", "bitcoin"),
    ETH("ETH", "Ethereum", "ETHBRL", "ethereum"),
    SOL("SOL", "Solana", "SOLBRL", "solana"),
    XRP("XRP", "XRP", "XRPBRL", "ripple"),
    ADA("ADA", "Cardano", "ADABRL", "cardano"),
    DOGE("DOGE", "Dogecoin", "DOGEBRL", "dogecoin"),
    ;

    companion object {
        val supported: List<Asset> = entries
        private val bySymbol = entries.associateBy(Asset::symbol)

        fun fromSymbol(symbol: String): Asset? = bySymbol[symbol.uppercase()]
    }
}
