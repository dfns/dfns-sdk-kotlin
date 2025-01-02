package co.dfns.sdk.tutorial.mobile.ui

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.dfns.androidsdk.PasskeysSigner
import co.dfns.androidsdk.model.RelyingParty
import co.dfns.sdk.tutorial.mobile.Constants.DFNS_APP_ID
import co.dfns.sdk.tutorial.mobile.Constants.PASSKEY_RELYING_PARTY_ID
import co.dfns.sdk.tutorial.mobile.Constants.PASSKEY_RELYING_PARTY_NAME
import co.dfns.sdk.tutorial.mobile.Server
import co.dfns.sdk.tutorial.mobile.Server.Wallet
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun DelegatedRegistrationPage(
    activity: Activity = ComponentActivity(),
    username: MutableState<String> = mutableStateOf(""),
    wallet: MutableState<Wallet?> = mutableStateOf(null)
) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val relyingParty = RelyingParty(id = PASSKEY_RELYING_PARTY_ID, name = PASSKEY_RELYING_PARTY_NAME)
    val signer = PasskeysSigner(activity, relyingParty)
    val server = Server()

    val registrationResponse = remember { mutableStateOf("") }

    fun register() {
        CoroutineScope(Dispatchers.IO).launch {
            val initResponse = server.registerInit(appId = DFNS_APP_ID, username = username.value)

            val fido2Attestation = signer.register(challenge = initResponse)

            val completeResponse = server.registerComplete(
                DFNS_APP_ID,
                fido2Attestation,
                temporaryAuthenticationToken = initResponse.temporaryAuthenticationToken
            )

            registrationResponse.value = gson.toJson(completeResponse)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 48.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Delegated Registration",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "For this tutorial, you will register a Dfns EndUser, and this is where the registration flow starts. However, in your final app, the flow may be different and the username might come from your internal system.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "After registration, the new end user will have an Ethereum testnet wallet and assigned the system permission, `DfnsDefaultEndUserAccess`, that grants the end user full access to their wallets.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Enter the email as the username you are registering, and hit the \"Register EndUser\" button.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email, contentDescription = "emailIcon"
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            onClick = { register() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Register User")
        }

        Row(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxWidth()
        ) {
            Text(
                text = registrationResponse.value,
                modifier = Modifier.padding(all = 16.dp),
                color = Color.White
            )
        }
    }
}
