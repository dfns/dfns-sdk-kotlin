package co.dfns.androidsdk

import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import co.dfns.androidsdk.model.CreatePasskeyResponseData
import co.dfns.androidsdk.model.Fido2Assertion
import co.dfns.androidsdk.model.Fido2AssertionData
import co.dfns.androidsdk.model.Fido2Attestation
import co.dfns.androidsdk.model.Fido2AttestationData
import co.dfns.androidsdk.model.GetPasskeyRequest
import co.dfns.androidsdk.model.GetPasskeyResponseData
import co.dfns.androidsdk.model.UserActionChallenge
import co.dfns.androidsdk.model.UserRegistrationChallenge
import co.dfns.androidsdk.model.RelyingParty
import com.google.gson.Gson

class PasskeysSigner {
    private val gson = Gson()
    private val context: Context
    private val credentialManager: CredentialManager
    private val relyingParty: RelyingParty

    constructor(context: Context, relyingParty: RelyingParty) {
        this.context = context
        this.relyingParty = relyingParty
        this.credentialManager = CredentialManager.create(context)
    }

    suspend fun register(
        challenge: UserRegistrationChallenge
    ): Fido2Attestation {
        val response = credentialManager.createCredential(
            context,
            CreatePublicKeyCredentialRequest(
                gson.toJson(
                    challenge.copy(
                        user = challenge.user.copy(
                            id = challenge.user.id.toByteArray().b64UrlEncode()
                        ),
                        rp = relyingParty
                    )
                )
            ),
        )

        val responseData = gson.fromJson(
            (response as CreatePublicKeyCredentialResponse).registrationResponseJson,
            CreatePasskeyResponseData::class.java
        )

        val fido2AttestationData = Fido2AttestationData(
            attestationData = responseData.response.attestationObject,
            clientData = responseData.response.clientDataJSON,
            credId = responseData.rawId,
        )

        val fido2Attestation = Fido2Attestation(
            credentialInfo = fido2AttestationData, credentialKind = "Fido2"
        )

        return fido2Attestation
    }

    suspend fun sign(challenge: UserActionChallenge): Fido2Assertion {
        val option = GetPublicKeyCredentialOption(
            gson.toJson(
                GetPasskeyRequest(
                    challenge = challenge.challenge,
                    allowCredentials = challenge.allowCredentials.webauthn.map {
                        GetPasskeyRequest.AllowCredentials(
                            id = it.id,
                            type = it.type,
                        )
                    },
                    timeout = 1800000,
                    userVerification = challenge.userVerification,
                    rpId = relyingParty.id,
                )
            )
        )
        val getCredRequest = GetCredentialRequest(listOf(option))
        val response = credentialManager.getCredential(context, getCredRequest)
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
