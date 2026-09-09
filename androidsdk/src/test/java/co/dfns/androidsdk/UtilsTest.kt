package co.dfns.androidsdk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [ByteArray.b64UrlEncode].
 *
 * The extension relies on [android.util.Base64], so the tests run under
 * [RobolectricTestRunner] to have the Android framework classes resolve on the JVM.
 */
@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    @Test
    fun `encodes known ascii vector without padding or wrapping`() {
        // Standard base64 of "hello" is "aGVsbG8=" — URL-safe + NO_PADDING drops the '='.
        val encoded = "hello".toByteArray(Charsets.UTF_8).b64UrlEncode()

        assertEquals("aGVsbG8", encoded)
    }

    @Test
    fun `output is unpadded, unwrapped and url-safe`() {
        // A longer input so standard base64 would normally emit '=' padding and,
        // for large inputs, a trailing newline via the default (non-NO_WRAP) flag.
        val input = "The quick brown fox jumps over the lazy dog".toByteArray(Charsets.UTF_8)

        val encoded = input.b64UrlEncode()

        assertFalse("must not contain padding", encoded.contains("="))
        assertFalse("must not contain '+' (url-safe uses '-')", encoded.contains("+"))
        assertFalse("must not contain '/' (url-safe uses '_')", encoded.contains("/"))
        assertFalse("must not be line-wrapped", encoded.contains("\n"))
        assertFalse("must not be line-wrapped", encoded.contains("\r"))
    }

    @Test
    fun `url-safe alphabet replaces plus and slash with dash and underscore`() {
        // 0xFB 0xFF standard-base64-encodes to "+/8=" (contains both '+' and '/').
        // URL-safe encoding must yield "-_8" instead, unpadded.
        val bytes = byteArrayOf(0xFB.toByte(), 0xFF.toByte())

        val encoded = bytes.b64UrlEncode()

        assertEquals("-_8", encoded)
        assertFalse(encoded.contains("+"))
        assertFalse(encoded.contains("/"))
        assertFalse(encoded.contains("="))
    }

    @Test
    fun `empty input encodes to empty string`() {
        assertEquals("", ByteArray(0).b64UrlEncode())
    }
}
