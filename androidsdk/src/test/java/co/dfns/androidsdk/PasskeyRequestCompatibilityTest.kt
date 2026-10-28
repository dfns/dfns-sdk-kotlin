package co.dfns.androidsdk

import co.dfns.androidsdk.model.AllowCredentials
import co.dfns.androidsdk.model.AuthenticatorSelectionCriteria
import co.dfns.androidsdk.model.PublicKeyCredentialDescriptor
import co.dfns.androidsdk.model.PublicKeyCredentialParameters
import co.dfns.androidsdk.model.RelyingParty
import co.dfns.androidsdk.model.SupportedCredentialKinds
import co.dfns.androidsdk.model.UserActionChallenge
import co.dfns.androidsdk.model.UserInformation
import co.dfns.androidsdk.model.UserRegistrationChallenge
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Binary-compatibility guard for the androidx.credentials request builders.
 *
 * The SDK compiles against its minimum supported androidx.credentials version, but these tests run
 * with the newest supported version on the runtime classpath (`credentialsLatest` in the version
 * catalog). A constructor call that only exists in the older version fails here with
 * NoSuchMethodError, which is what apps resolving the newer version would hit at runtime.
 */
class PasskeyRequestCompatibilityTest {
    private val gson = Gson()
    private val relyingParty = RelyingParty(id = "example.com", name = "Example")

    @Test
    fun `builds the passkey creation request`() {
        val challenge = UserRegistrationChallenge(
            temporaryAuthenticationToken = "temp-token",
            user = UserInformation(id = "user-id", displayName = "Ada", name = "ada@example.com"),
            supportedCredentialKinds = SupportedCredentialKinds(listOf("Fido2"), listOf("Fido2")),
            otpUrl = "otp://example",
            challenge = "challenge-value",
            authenticatorSelection = AuthenticatorSelectionCriteria(
                residentKey = "required",
                requireResidentKey = true,
                userVerification = "required",
            ),
            attestation = "none",
            pubKeyCredParams = listOf(PublicKeyCredentialParameters(type = "public-key", alg = -7)),
            excludeCredentials = emptyList(),
        )

        val request = createPasskeyRequest(gson, challenge, relyingParty)

        assertTrue(request.requestJson.contains("\"challenge\":\"challenge-value\""))
        assertTrue(request.requestJson.contains("\"id\":\"example.com\""))
    }

    @Test
    fun `builds the passkey assertion request`() {
        val challenge = UserActionChallenge(
            attestation = "none",
            userVerification = "required",
            externalAuthenticationUrl = "",
            challenge = "challenge-value",
            challengeIdentifier = "chal-123",
            supportedCredentialKinds = emptyList(),
            allowCredentials = AllowCredentials(
                webauthn = listOf(PublicKeyCredentialDescriptor(type = "public-key", id = "cred-1")),
                key = emptyList(),
            ),
        )

        val request = getPasskeyRequest(gson, challenge, relyingParty, timeout = 60_000L)

        assertEquals(1, request.credentialOptions.size)
    }
}
