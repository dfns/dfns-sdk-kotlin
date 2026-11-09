package co.dfns.androidsdk

import android.content.ContextWrapper
import androidx.credentials.CreatePasswordResponse
import androidx.credentials.PasswordCredential
import co.dfns.androidsdk.model.RelyingParty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PasskeysSignerTest {
    private val relyingParty = RelyingParty(id = "example.com", name = "Example")

    @Test
    fun `rejects a zero timeout`() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            PasskeysSigner(
                context = ContextWrapper(null),
                relyingParty = relyingParty,
                timeout = 0,
            )
        }
        assertEquals("timeout must be positive", error.message)
    }

    @Test
    fun `rejects a registration response that is not a public key`() {
        val error = assertThrows(IllegalStateException::class.java) {
            requirePublicKeyRegistration(CreatePasswordResponse())
        }
        assertEquals(
            "Expected a public key credential response, got CreatePasswordResponse",
            error.message,
        )
    }

    @Test
    fun `rejects an assertion credential that is not a public key`() {
        val error = assertThrows(IllegalStateException::class.java) {
            requirePublicKeyAssertion(PasswordCredential("user", "secret"))
        }
        assertEquals(
            "Expected a public key credential, got PasswordCredential",
            error.message,
        )
    }
}
