package org.kethereum.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.math.BigInteger.*

class TheBigIntegerExtensions {

    @Test
    fun paddingWorks() {
        assertThat(BigInteger("5").toBytesPadded(42).size).isEqualTo(42)
    }

    @Test
    fun maybeHexToBigIntegerWorks() {
        assertThat("0xa".maybeHexToBigInteger()).isEqualTo(TEN)
        assertThat("10".maybeHexToBigInteger()).isEqualTo(TEN)
        assertThat("0x0".maybeHexToBigInteger()).isEqualTo(ZERO)
        assertThat("0".maybeHexToBigInteger()).isEqualTo(ZERO)
        assertThat("0x1".maybeHexToBigInteger()).isEqualTo(ONE)
        assertThat("1".maybeHexToBigInteger()).isEqualTo(ONE)
        assertThat("1001".maybeHexToBigInteger()).isEqualTo(1001)

        assertThrows(NumberFormatException::class.java) {
            "a".maybeHexToBigInteger()
        }
        assertThrows(NumberFormatException::class.java) {
            "0x?".maybeHexToBigInteger()
        }
        assertThrows(NumberFormatException::class.java) {
            "yolo".maybeHexToBigInteger()
        }
    }
}
