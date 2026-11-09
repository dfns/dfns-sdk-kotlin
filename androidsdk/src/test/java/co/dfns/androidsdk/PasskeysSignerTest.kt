package co.dfns.androidsdk

import android.content.ContextWrapper
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
}
