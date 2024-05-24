package co.dfns.androidsdk

import android.util.Base64

fun ByteArray.b64UrlEncode(): String {
    return Base64.encodeToString(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}
