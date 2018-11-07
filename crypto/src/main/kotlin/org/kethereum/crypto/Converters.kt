package org.kethereum.crypto

import org.kethereum.crypto.ec.CurvePoint
import org.kethereum.crypto.model.*
import org.kethereum.extensions.toHexStringZeroPadded
import org.kethereum.keccakshortcut.keccak
import org.kethereum.model.Address
import org.walleth.khex.hexToByteArray
import org.walleth.khex.toHexString
import java.math.BigInteger
import java.util.*


fun PublicKey.toAddress() : Address {
    val publicKeyHexString = key.toHexStringZeroPadded(PUBLIC_KEY_LENGTH_IN_HEX, false)
    val hexToByteArray = publicKeyHexString.hexToByteArray()
    val hash = hexToByteArray.keccak().toHexString()

    return Address(hash.substring(hash.length - ADDRESS_LENGTH_IN_HEX))  // right most 160 bits
}

fun ECKeyPair.toAddress() = publicKey.toAddress()

fun ECKeyPair.toCredentials() = Credentials(this, toAddress())

fun PrivateKey.toECKeyPair() = ECKeyPair(this, publicKeyFromPrivate(this))


/**
 * Decodes an uncompressed public key (without 0x04 prefix) given an ECPoint
 */
fun CurvePoint.toPublicKey() = encoded().let { encoded ->
    PublicKey(BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.size)))
}
