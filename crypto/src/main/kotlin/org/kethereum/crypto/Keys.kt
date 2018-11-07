package org.kethereum.crypto

import org.kethereum.crypto.SecureRandomUtils.secureRandom
import org.kethereum.crypto.model.ECKeyPair
import org.kethereum.crypto.model.PUBLIC_KEY_SIZE
import org.kethereum.extensions.toBytesPadded
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.util.*

fun initializeCrypto() {
    Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
}
/**
 * Create a keypair using SECP-256k1 curve.
 *
 *
 * Private keypairs are encoded using PKCS8
 *
 *
 * Private keys are encoded using X.509
 */
internal fun createSecp256k1KeyPair(): KeyPair {

    val keyPairGenerator = KeyPairGenerator.getInstance("ECDSA")
    val ecGenParameterSpec = ECGenParameterSpec("secp256k1")
    keyPairGenerator.initialize(ecGenParameterSpec, secureRandom())
    return keyPairGenerator.generateKeyPair()
}

fun createEthereumKeyPair() = createSecp256k1KeyPair().toECKeyPair()

fun ECKeyPair.getCompressedPublicKey(): ByteArray {
    //add the uncompressed prefix
    val ret = publicKey.key.toBytesPadded(PUBLIC_KEY_SIZE + 1)
    ret[0] = 4
    val point = CURVE.decodePoint(ret)
    return point.encoded(true)
}

/**
 * Takes a public key in compressed encoding (including prefix)
 * and returns the key in uncompressed encoding (without prefix)
 */
fun decompressKey(publicBytes: ByteArray): ByteArray {
    val point = CURVE.decodePoint(publicBytes)
    val encoded = point.encoded()
    return Arrays.copyOfRange(encoded, 1, encoded.size)
}
