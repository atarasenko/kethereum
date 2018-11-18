package org.kethereum.crypto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kethereum.crypto.data.ADDRESS
import org.kethereum.crypto.data.KEY_PAIR

class CredentialsTest {

    @Test
    fun testCredentialsFromString() {
        val credentials = KEY_PAIR.toCredentials()

        assertThat(credentials.address).isEqualTo(ADDRESS)
        assertThat(credentials.ecKeyPair).isEqualToComparingFieldByField(KEY_PAIR)
   }
}
