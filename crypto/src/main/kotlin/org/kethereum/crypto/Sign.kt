package org.kethereum.crypto

import org.kethereum.crypto.api.ec.*
import org.kethereum.crypto.model.ECKeyPair
import org.kethereum.crypto.model.PrivateKey
import org.kethereum.crypto.model.PublicKey
import org.kethereum.keccakshortcut.keccak
import org.kethereum.model.SignatureData
import java.security.SignatureException
import kotlin.experimental.and

/**
 *
 * Transaction signing logic.
 *
 *
 * Adapted from the
 * [BitcoinJ ECKey](https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/ECKey.java) implementation.
 */
val CURVE: Curve = curve()

/**
 * Signs the [keccak] hash of the [message] buffer.
 * The signature is canonicalised ( @see [ECDSASignature.toCanonicalised] )
 *
 * @return [SignatureData] containing the (r,s,v) components
 */

fun ECKeyPair.signMessage(message: ByteArray) =
    signMessageHash(message.keccak(), this, true)


/**
 * Signs the the [messageHash] buffer with the private key in the given [keyPair].
 * If the [toCanonical] param is true, the signature is canonicalised ( @see [ECDSASignature.toCanonicalised] )
 *
 * This method is provided to allow for more flexible signature schemes using the same Ethereum keys.
 *
 * @return [SignatureData] containing the (r,s,v) components
 */
fun signMessageHash(messageHash: ByteArray, keyPair: ECKeyPair, toCanonical: Boolean = true): SignatureData {
    val privateKey = keyPair.privateKey
    val publicKey = keyPair.publicKey
    val signature = sign(messageHash, privateKey, toCanonical)

    // Now we have to work backwards to figure out the recId needed to recover the public key
    val recId = signature.determineRecId(messageHash, publicKey)

    val headerByte = recId + 27

    val v = headerByte.toByte()

    return SignatureData(signature.r, signature.s, v)
}

fun ECDSASignature.determineRecId(messageHash: ByteArray, publicKey: PublicKey): Int {
    val signer = signer()
    for (i in 0..3) {
        val k = signer.recover(i, this, messageHash)
        if (k != null && k == publicKey.key) {
            return i
        }
    }
    throw RuntimeException("Could not construct a recoverable key. This should never happen.")
}

private fun sign(transactionHash: ByteArray, privateKey: PrivateKey, canonical: Boolean): ECDSASignature =
    signer().sign(transactionHash, privateKey.key).let {
        if (canonical) {
            it.toCanonicalised()
        } else {
            it
        }
    }

/**
 * Given an arbitrary piece of text and an Ethereum message signature encoded in bytes,
 * returns the public key that was used to sign it. This can then be compared to the expected
 * public key to determine if the signature was correct.
 *
 * @param message RLP encoded message.
 * @param signatureData The message signature components
 * @return the public key used to sign the message
 * @throws SignatureException If the public key could not be recovered or if there was a
 * signature format error.
 */
@Throws(SignatureException::class)
fun signedMessageToKey(message: ByteArray, signatureData: SignatureData): PublicKey {

    val header = signatureData.v and 0xFF.toByte()
    // The header byte: 0x1B = first key with even y, 0x1C = first key with odd y,
    //                  0x1D = second key with even y, 0x1E = second key with odd y
    if (header < 27 || header > 34) {
        throw SignatureException("Header byte out of range: $header")
    }

    val sig = ECDSASignature(signatureData.r, signatureData.s)

    val messageHash = message.keccak()
    val recId = header - 27
    return PublicKey(
        signer().recover(recId, sig, messageHash) ?: throw SignatureException("Could not recover public key from signature")
    )
}

/**
 * Returns public key from the given private key.
 *
 * @param privateKey the private key to derive the public key from
 * @return BigInteger encoded public key
 */
fun publicKeyFromPrivate(privateKey: PrivateKey): PublicKey {
    return PublicKey(signer().publicFromPrivate(privateKey.key))
}
