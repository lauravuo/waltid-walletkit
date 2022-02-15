package id.walt.webwallet.backend.config

import com.beust.klaxon.Klaxon
import java.io.File

data class WalletConfig(
  val walletUiUrl: String = "http://localhost:3000",
  val walletApiUrl: String = "http://localhost:3000/api",
  val issuers: Map<String, IssuerConfiguration> = IssuerConfiguration.getDefaultIssuerConfigurations()
) {
  companion object {
    val CONFIG_FILE = "${id.walt.WALTID_DATA_ROOT}/config/wallet-config.json"
    lateinit var config: WalletConfig
    init {
      val cf = File(CONFIG_FILE)
      if(cf.exists()) {
        config = Klaxon().parse<WalletConfig>(cf) ?: WalletConfig()
      } else {
        config = WalletConfig()
      }
    }
  }
}

