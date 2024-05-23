package co.dfns.androidsdk.model

data class GetPasskeyRequest(
    val challenge: String,
    val allowCredentials: AllowCredentials,
    val timeout: Long,
    val userVerification: String,
    val rpId: String,
)