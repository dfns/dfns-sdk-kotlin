package co.dfns.androidsdk.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Gson round-trip and deserialization tests for the DFNS API model data classes.
 *
 * These are pure JVM tests: Gson does not touch the Android framework, so no
 * Robolectric runner is required here.
 */
class ModelSerializationTest {

    private val gson = Gson()

    private inline fun <reified T> roundTrip(value: T): T =
        gson.fromJson(gson.toJson(value), T::class.java)

    @Test
    fun `CreatePasskeyResponseData round-trips`() {
        val original = CreatePasskeyResponseData(
            response = CreatePasskeyResponseData.Response(
                clientDataJSON = "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIn0",
                attestationObject = "o2NmbXRkbm9uZQ",
                transports = listOf("internal", "hybrid"),
            ),
            authenticatorAttachment = "platform",
            id = "credential-id",
            rawId = "cmF3LWlk",
            type = "public-key",
        )

        assertEquals(original, roundTrip(original))
    }

    @Test
    fun `CreatePasskeyResponseData deserializes from an API-shaped literal`() {
        val json = """
            {
              "response": {
                "clientDataJSON": "client-data",
                "attestationObject": "attestation-object",
                "transports": ["internal", "usb"]
              },
              "authenticatorAttachment": "cross-platform",
              "id": "id-value",
              "rawId": "raw-id-value",
              "type": "public-key"
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, CreatePasskeyResponseData::class.java)

        assertEquals("client-data", parsed.response.clientDataJSON)
        assertEquals("attestation-object", parsed.response.attestationObject)
        assertEquals(listOf("internal", "usb"), parsed.response.transports)
        assertEquals("cross-platform", parsed.authenticatorAttachment)
        assertEquals("id-value", parsed.id)
        assertEquals("raw-id-value", parsed.rawId)
        assertEquals("public-key", parsed.type)
    }

    @Test
    fun `GetPasskeyResponseData deserializes from an API-shaped literal`() {
        val json = """
            {
              "response": {
                "clientDataJSON": "client-data",
                "authenticatorData": "authenticator-data",
                "signature": "signature-value",
                "userHandle": "user-handle"
              },
              "authenticatorAttachment": "platform",
              "id": "id-value",
              "rawId": "raw-id-value",
              "type": "public-key"
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, GetPasskeyResponseData::class.java)

        assertEquals("client-data", parsed.response.clientDataJSON)
        assertEquals("authenticator-data", parsed.response.authenticatorData)
        assertEquals("signature-value", parsed.response.signature)
        assertEquals("user-handle", parsed.response.userHandle)
        assertEquals("raw-id-value", parsed.rawId)
    }

    @Test
    fun `GetPasskeyRequest serializes to the expected WebAuthn shape`() {
        val request = GetPasskeyRequest(
            challenge = "challenge-value",
            allowCredentials = listOf(
                GetPasskeyRequest.AllowCredentials(id = "cred-1", type = "public-key"),
            ),
            timeout = 1_800_000L,
            userVerification = "required",
            rpId = "example.com",
        )

        val reparsed = roundTrip(request)

        assertEquals(request, reparsed)
        assertEquals(1_800_000L, reparsed.timeout)
        assertEquals("example.com", reparsed.rpId)
        assertEquals(1, reparsed.allowCredentials.size)
        assertEquals("cred-1", reparsed.allowCredentials[0].id)
    }

    @Test
    fun `UserRegistrationChallenge deserializes with a null relying party`() {
        val json = """
            {
              "temporaryAuthenticationToken": "temp-token",
              "user": { "id": "user-id", "displayName": "Ada", "name": "ada@example.com" },
              "supportedCredentialKinds": {
                "firstFactor": ["Fido2"],
                "secondFactor": ["Fido2"]
              },
              "otpUrl": "otp://example",
              "challenge": "challenge-value",
              "authenticatorSelection": {
                "residentKey": "required",
                "requireResidentKey": true,
                "userVerification": "required"
              },
              "attestation": "none",
              "pubKeyCredParams": [ { "type": "public-key", "alg": -7 } ],
              "excludeCredentials": []
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, UserRegistrationChallenge::class.java)

        assertNull("rp is optional and absent here", parsed.rp)
        assertEquals("temp-token", parsed.temporaryAuthenticationToken)
        assertEquals("user-id", parsed.user.id)
        assertEquals(listOf("Fido2"), parsed.supportedCredentialKinds.firstFactor)
        assertEquals(-7, parsed.pubKeyCredParams[0].alg)
        assertEquals(true, parsed.authenticatorSelection.requireResidentKey)
        assertEquals(0, parsed.excludeCredentials.size)
    }

    @Test
    fun `UserActionAssertion round-trips through Fido2 assertion`() {
        val assertion = UserActionAssertion(
            challengeIdentifier = "challenge-id",
            firstFactor = Fido2Assertion(
                kind = "Fido2",
                credentialAssertion = Fido2AssertionData(
                    clientData = "client-data",
                    credId = "cred-id",
                    signature = "signature",
                    authenticatorData = "authenticator-data",
                    userHandle = null,
                ),
            ),
        )

        val reparsed = roundTrip(assertion)

        assertEquals(assertion, reparsed)
        assertNull(reparsed.firstFactor.credentialAssertion.userHandle)
    }
}
