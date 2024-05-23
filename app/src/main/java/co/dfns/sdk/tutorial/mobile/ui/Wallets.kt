package co.dfns.sdk.tutorial.mobile.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.dfns.androidsdk.PasskeysSigner
import co.dfns.androidsdk.model.UserActionAssertion
import co.dfns.sdk.tutorial.mobile.Constants.APP_ID
import co.dfns.sdk.tutorial.mobile.Server
import co.dfns.sdk.tutorial.mobile.Server.Wallet
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun WalletsPage(
    activity: Activity = Activity(),
    token: MutableState<String> = mutableStateOf(""),
) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val server = Server()
    val signer = PasskeysSigner(activity)

    val walletId = remember { mutableStateOf("listOf<Wallet>()") }
    val wallets = remember { mutableStateOf(listOf<Wallet>()) }
    val message = remember { mutableStateOf("") }
    val signatureResponse = remember { mutableStateOf("{}") }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val resp = server.listWallets(APP_ID, token.value)

            walletId.value = resp.items[0].id
            wallets.value = resp.items
        }
    }

    fun sign() {
        CoroutineScope(Dispatchers.IO).launch {
            val initResponse = server.initSignature(
                message = message.value,
                walletId = walletId.value,
                appId = APP_ID,
                authToken = token.value
            )

            val fido2Assertion = signer.sign(
                challenge = initResponse.challenge
            )

            val userActionAssertion = UserActionAssertion(
                challengeIdentifier = initResponse.challenge.challengeIdentifier,
                firstFactor = fido2Assertion
            )
            val completeResponse = server.completeSignature(
                walletId = walletId.value,
                appId = APP_ID,
                authToken = token.value,
                requestBody = initResponse.requestBody,
                signedChallenge = userActionAssertion
            )

            signatureResponse.value = gson.toJson(completeResponse)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "End User Wallets",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "The Ethereum testnet wallet created for the end user during registration is listed below. Listing wallets only needs the readonly auth token. End users won't be prompted to use their WebAuthn credentials.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Row(
            modifier = Modifier
                .background(color = Color.Black)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = gson.toJson(wallets.value),
                modifier = Modifier.padding(all = 16.dp),
                color = Color.White
            )
        }

        Text(
            "Use wallets to broadcast transactions will require the end users to sign a challenge each time to authorize the action. For this tutorial, because new wallets do not have any native tokens to pay for gas fees, we won't be able to broadcast any transactions to chain. Instead, we will sign an arbitrary message that can be used as proof the end user is the owner of the private key secured by Dfns.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "Enter a message in the input box and press the \"Sign Message\" button. You will see a WebAuthn prompt asking for authorization to perform the action. Once granted, the tutorial makes a request to Dfns MPC signers and gets a signature hash. Optionally you can use etherscan to verify this signature hash matches the wallet address.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            label = { Text(text = "Enter your message") },
            value = message.value,
            onValueChange = { message.value = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Create, contentDescription = "emailIcon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp), onClick = {
                sign()
            }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Sign Message")
        }

        Row(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxWidth()
        ) {
            Text(
                text = signatureResponse.value,
                modifier = Modifier.padding(all = 16.dp),
                color = Color.White
            )
        }
    }
}
