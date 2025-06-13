package co.dfns.sdk.tutorial.mobile

import co.dfns.androidsdk.model.Fido2Attestation
import co.dfns.androidsdk.model.UserActionAssertion
import co.dfns.androidsdk.model.UserActionChallenge
import co.dfns.androidsdk.model.UserRegistrationChallenge
import co.dfns.sdk.tutorial.mobile.Constants.SERVER_BASE_URL
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Server {
    private val gson = Gson()

    fun registerInit(
        username: String
    ): UserRegistrationChallenge {
        data class RegisterInit(
            val username: String,
        )

        val path = "/register/init"
        val postBody = gson.toJson(RegisterInit(username))

        return makeRequest<UserRegistrationChallenge>(path, postBody)
    }

    data class Credential(val uuid: String, val kind: String, val name: String)
    data class User(val id: String, val username: String, val orgId: String)
    data class Wallet(val id: String, val network: String)

    data class RegistrationCompletionResponse(
        val credential: Credential, val user: User, val wallets: List<Wallet>
    )

    fun registerComplete(
        fido2Attestation: Fido2Attestation, temporaryAuthenticationToken: String
    ): RegistrationCompletionResponse {
        data class SignedChallenge(
            val firstFactorCredential: Fido2Attestation,
        )

        data class RegisterComplete(
            val signedChallenge: SignedChallenge,
            val temporaryAuthenticationToken: String,
        )

        val path = "/register/complete"
        val postBody = gson.toJson(
            RegisterComplete(
                SignedChallenge(firstFactorCredential = fido2Attestation),
                temporaryAuthenticationToken
            )
        )

        return makeRequest<RegistrationCompletionResponse>(path, postBody)
    }

    data class LoginResponse(val username: String, val token: String)

    fun login(
        username: String,
    ): LoginResponse {
        data class LoginRequest(
            val username: String,
        )

        val path = "/login"
        val postBody = gson.toJson(
            LoginRequest(
                username
            )
        )

        return makeRequest<LoginResponse>(path, postBody)
    }

    data class ListWalletsResponse(
        val items: List<Wallet>
    )

    fun listWallets(
        authToken: String
    ): ListWalletsResponse {
        data class ListWalletsRequest(
            val authToken: String,
        )

        val path = "/wallets/list"
        val postBody = gson.toJson(
            ListWalletsRequest(
                authToken
            )
        )

        return makeRequest<ListWalletsResponse>(path, postBody)
    }

    data class InitSignatureRequestBody(
        val kind: String,
        val message: String,
    )

    data class InitSignatureResponse(
        val requestBody: InitSignatureRequestBody, val challenge: UserActionChallenge
    )

    fun initSignature(
        message: String,
        walletId: String,
        authToken: String,
    ): InitSignatureResponse {
        data class InitSignatureRequest(
            val message: String,
            val walletId: String,
            val authToken: String,
        )

        val path = "/wallets/signatures/init"
        val postBody = gson.toJson(
            InitSignatureRequest(
                message, walletId, authToken
            )
        )

        return makeRequest<InitSignatureResponse>(path, postBody)
    }

    data class Requester(
        val userId: String,
    )

    data class Signature(
        val r: String,
        val s: String,
        val recid: Int,
        val encoded: String,
    )

    data class CompleteSignatureResponse(
        val id: String,
        val walletId: String,
        val network: String,
        val requester: Requester,
        val requestBody: InitSignatureRequestBody,
        val status: String,
        val signature: Signature,
        val dateRequested: String,
        val dateSigned: String,
    )

    fun completeSignature(
        walletId: String,
        authToken: String,
        requestBody: InitSignatureRequestBody,
        signedChallenge: UserActionAssertion,
    ): CompleteSignatureResponse {
        data class CompleteSignatureRequest(
            val walletId: String,
            val authToken: String,
            val requestBody: InitSignatureRequestBody,
            val signedChallenge: UserActionAssertion,
        )

        val path = "/wallets/signatures/complete"
        val postBody = gson.toJson(
            CompleteSignatureRequest(
                walletId, authToken, requestBody, signedChallenge
            )
        )

        return makeRequest<CompleteSignatureResponse>(path, postBody)
    }

    private inline fun <reified T> makeRequest(
        path: String, postBody: String
    ): T {
        val connection = URL("${SERVER_BASE_URL}${path}").openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.setDoOutput(true)

        connection.outputStream.use { os ->
            val input: ByteArray = postBody.toByteArray()
            os.write(input, 0, input.size)
        }

        BufferedReader(
            InputStreamReader(connection.inputStream, "utf-8")
        ).use { br ->
            val response = StringBuilder()
            var responseLine: String? = null
            while ((br.readLine().also { responseLine = it }) != null) {
                response.append(responseLine!!.trim { it <= ' ' })
            }

            return gson.fromJson(response.toString(), T::class.java)
        }
    }
}
