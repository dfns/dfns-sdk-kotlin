package co.dfns.androidsdk

import java.util.Base64

// URL-safe, unpadded, unwrapped base64 — matching the previous
// android.util.Base64 (URL_SAFE or NO_PADDING or NO_WRAP) behaviour.
// java.util.Base64 is available on all supported devices (minSdk 28; added in API 26)
// and, being pure JVM, keeps this testable without the Android framework.
fun ByteArray.b64UrlEncode(): String {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(this)
}
