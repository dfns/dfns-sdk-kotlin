package co.dfns.androidsdk.model

data class RelyingParty(
    val id: String, val name: String
)

data class UserInformation(
    val id: String, val displayName: String, val name: String
)

data class SupportedCredentialKinds(
    val firstFactor: List<String>,
    val secondFactor: List<String>,
)

data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: String? = "",
    val residentKey: String,
    val requireResidentKey: Boolean,
    val userVerification: String,
)

data class PublicKeyCredentialParameters(
    val type: String, val alg: Int
)

data class PublicKeyCredentialDescriptor(
    val type: String,
    val id: String,
)

data class UserRegistrationChallenge(
    val temporaryAuthenticationToken: String,
    val rp: RelyingParty? = null
    val user: UserInformation,
    val supportedCredentialKinds: SupportedCredentialKinds,
    val otpUrl: String,
    val challenge: String,
    val authenticatorSelection: AuthenticatorSelectionCriteria,
    val attestation: String,
    val pubKeyCredParams: List<PublicKeyCredentialParameters>,
    val excludeCredentials: List<PublicKeyCredentialDescriptor>
)

data class Fido2AttestationData(
    val attestationData: String,
    val clientData: String,
    val credId: String,
)

data class Fido2Attestation(
    val credentialInfo: Fido2AttestationData,
    val credentialKind: String,
)

data class AllowCredentials(
    val webauthn: List<PublicKeyCredentialDescriptor>, val key: List<PublicKeyCredentialDescriptor>
)

data class UserActionChallenge(
    val attestation: String,
    val userVerification: String,
    val externalAuthenticationUrl: String,
    val challenge: String,
    val challengeIdentifier: String,
    val rp: RelyingParty? = null
    val supportedCredentialKinds: List<SupportedCredentialKinds>,
    val allowCredentials: AllowCredentials
)

data class Fido2AssertionData(
    val clientData: String,
    val credId: String,
    val signature: String,
    val authenticatorData: String,
    val userHandle: String?
)

data class Fido2Assertion(
    val kind: String,
    val credentialAssertion: Fido2AssertionData,
)

data class UserActionAssertion(
    val challengeIdentifier: String, val firstFactor: Fido2Assertion
)
