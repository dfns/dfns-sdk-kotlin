package co.dfns.sdk.tutorial.mobile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.dfns.sdk.tutorial.mobile.R

@Composable
@Preview
fun HomePage(
    onDelegatedRegistration: () -> Unit = {},
    onDelegatedLogin: () -> Unit = {},
    onWallets: () -> Unit = {},
    token: MutableState<String> = mutableStateOf(""),
) {
    val logo = painterResource(R.drawable.logo)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Image(
            painter = logo,
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

        /// Introduction

        Text(
            "Introduction",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "This tutorial app demonstrates how to use Dfns SDK in the following configuration:\n\n\u2022You have a server and a web single page application.\n\u2022You are not a custodian, and your customers own their wallets.\n\u2022Your customers will use WebAuthn (preferred) or a key credential (discourage as it comes with security risks) credentials to authenticate with Dfns.\n\u2022Your client applications communicates with your server, and does not call the Dfns API directly.\n\u2022Your server communicates with the Dfns API using a service account.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        /// Step 1

        Text(
            "Step 1 - Delegated Registration",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "Your customers, either new or existing, must register with Dfns first and have credential(s) in our system in order to own and be able to interact with their blockchain wallets.\n\nThe delegated registration flow allows you to initiate and and complete the registration process on your customers behalf, without them being aware that the wallets infrastructure is powered by Dfns, i.e. they will not receive an registration email from Dfns directly unlike the normal registration process for your employees. Their WebAuthn credentials are still completely under their control.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            onClick = { onDelegatedRegistration() },
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Go to Delegated Registration")
        }

        /// Step 2

        Text(
            "Step 2 - Delegated Login",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "The delegated signing flow does not need the end user sign with the WebAuthn credential. The login can be performed on the server side transparent to the end users and obtain a readonly auth token. For example, your server can choose to automatically login the end users upon the completion of delegated registration. In this tutorial, this step is shown as explicit in order to more clearly demonstrate how the interaction works.",
            modifier = Modifier.padding(
                bottom = 16.dp
            )
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            onClick = { onDelegatedLogin() },
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Go to Delegated Login")
        }

        /// STEP 3

        Text(
            "Step 3 - Wallets",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "Once logged in, the end users can use the wallets they own.",
            modifier = Modifier.padding(
                bottom = 16.dp
            )
        )

        if (token.value.isNotEmpty()) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                onClick = { onWallets() },
                shape = RoundedCornerShape(30),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Go to Wallets")
            }
        } else {
            Text(
                "⚠️ You need to complete step 1 and 2 first", modifier = Modifier.padding(
                    bottom = 16.dp
                )
            )
        }

        Text(
            "The end 🎉",
            modifier = Modifier.padding(
                bottom = 16.dp
            ),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}
