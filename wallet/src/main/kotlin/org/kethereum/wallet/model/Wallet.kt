package org.kethereum.wallet.model

import com.squareup.moshi.Json

const val AES_128_CTR = "pbkdf2"
const val SCRYPT = "scrypt"

data class CipherParams(var iv: String)

data class WalletCrypto(
        var cipher: String,
        var ciphertext: String,
        var cipherparams: CipherParams,
        var kdf: String,
        var kdfparams: KdfParams,
        var mac: String)


internal data class WalletForImport(

        var address: String? = null,
        var addresses: Map<String, String> = emptyMap(),

        var crypto: WalletCrypto? = null,

        @field:Json(name = "Crypto") //for MyEtherWallet json
        var cryptoFromMEW: WalletCrypto? = null,

        var id: String? = null,
        var version: Int = 0
)

interface Wallet {
    val id: String
    val version: Int
    val crypto: WalletCrypto
}

class WalletV4(
        val addresses: Map<String, String>,
        override val version: Int = 4,
        override val id: String,
        override val crypto: WalletCrypto
) : Wallet

class WalletV3(
        val address: String,
        override val version: Int = 3,
        override val id: String,
        override val crypto: WalletCrypto
) : Wallet
