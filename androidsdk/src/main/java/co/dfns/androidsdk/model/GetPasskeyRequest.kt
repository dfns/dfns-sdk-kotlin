package co.dfns.androidsdk.model

data class GetPasskeyRequest(
    val challenge: String,
    val allowCredentials: List<AllowCredentials>,
    val timeout: Long,
    val userVerification: String,
    val rpId: String,
) {
    data class AllowCredentials(
        val id: String,
        val type: String,
    )
}
