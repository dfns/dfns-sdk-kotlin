package co.dfns.sdkandroid

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import co.dfns.sdkandroid.model.CreatePasskeyRequest
import co.dfns.sdkandroid.model.CreatePasskeyResponseData
import co.dfns.sdkandroid.model.Fido2Assertion
import co.dfns.sdkandroid.model.GetPasskeyResponseData
import co.dfns.sdkandroid.model.UserRegistrationChallenge
import co.dfns.sdkandroid.model.Fido2AssertionData
import co.dfns.sdkandroid.model.Fido2Attestation
import co.dfns.sdkandroid.model.Fido2AttestationData
import co.dfns.sdkandroid.model.GetPasskeyRequest
import com.google.gson.Gson

class PasskeysSigner {
    private val gson = Gson()

    suspend fun register(
        activity: Activity, challenge: UserRegistrationChallenge
    ): Fido2Attestation {
        val credentialManager = CredentialManager.create(activity)

        val response = credentialManager.createCredential(
            activity,
            CreatePublicKeyCredentialRequest(
                gson.toJson(
                    CreatePasskeyRequest(
                        challenge = challenge.challenge,
                        rp = CreatePasskeyRequest.Rp(
                            name = challenge.rp.name, id = challenge.rp.id
                        ),
                        user = CreatePasskeyRequest.User(
                            id = challenge.user.id.toByteArray().b64Encode(),
                            name = challenge.user.name,
                            displayName = challenge.user.displayName,
                        ),
                        pubKeyCredParams = challenge.pubKeyCredParams,
                        timeout = 1800000,
                        attestation = "none",
                        excludeCredentials = emptyList(),
                        authenticatorSelection = CreatePasskeyRequest.AuthenticatorSelection(
                            authenticatorAttachment = "platform",
                            requireResidentKey = false,
                            residentKey = "required",
                            userVerification = "required"
                        )
                    )
                )
            ),
        )

        val responseData = gson.fromJson(
            (response as CreatePublicKeyCredentialResponse).registrationResponseJson,
            CreatePasskeyResponseData::class.java
        )

        val fido2AttestationData = Fido2AttestationData(
            attestationData = responseData.response.attestationObject, // TODO: b64url?
            clientData = responseData.response.clientDataJSON,
            credId = responseData.rawId,
        )

        val fido2Attestation = Fido2Attestation(
            credentialInfo = fido2AttestationData, credentialKind = "Fido2"
        )

        return fido2Attestation
    }

    suspend fun sign(activity: Activity, challenge: String, rpId: String): Fido2Assertion {
        val credentialManager = CredentialManager.create(activity)

        val option = GetPublicKeyCredentialOption(
            gson.toJson(
                GetPasskeyRequest(
                    challenge,
                    timeout = 1800000,
                    userVerification = "required",
                    rpId = rpId,
                    allowCredentials = listOf()
                )
            )
        )
        val getCredRequest = GetCredentialRequest(listOf(option))
        val response = credentialManager.getCredential(activity, getCredRequest)
        val cred = response.credential as PublicKeyCredential

        val passkeyResponse =
            gson.fromJson(cred.authenticationResponseJson, GetPasskeyResponseData::class.java)

        val fido2AssertionData = Fido2AssertionData(
            clientData = passkeyResponse.response.clientDataJSON,
            credId = passkeyResponse.rawId,
            signature = passkeyResponse.response.signature,
            authenticatorData = passkeyResponse.response.authenticatorData,
            userHandle = passkeyResponse.response.userHandle,
        )

        val fido2Assertion = Fido2Assertion(
            credentialAssertion = fido2AssertionData, kind = "Fido2"
        )

        return fido2Assertion
    }
}

