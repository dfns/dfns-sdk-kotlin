package co.dfns.sdk.tutorial.mobile.ui

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
import co.dfns.sdk.tutorial.mobile.Server
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun DelegatedLoginPage(
    token: MutableState<String> = mutableStateOf(""),
    username: MutableState<String> = mutableStateOf("")
) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val server = Server()

    val loginResponse = remember { mutableStateOf("") }

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val resp = server.login(username.value)

            loginResponse.value = gson.toJson(resp)
            token.value = resp.token
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 48.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Delegated Login",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "For this tutorial, the delegated login flow is started on the client side by pressing the \"Login EndUser\" button. A request is sent to the server and a readonly auth token is returned in the response. This flow does not need users to sign with the WebAuthn credential.",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            "This auth token is readonly and needs to be cached and passed along with all requests interacting with the Dfns API. To clearly demonstrate all the necessary components for each step, this example will cache the auth token in the application context and send it back with every sequently request to the server. You should however choose a more secure caching method.",
            modifier = Modifier.padding(bottom = 16.dp)
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
            onClick = { login() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Login EndUser")
        }

        Row(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxWidth()
        ) {
            Text(
                text = loginResponse.value,
                modifier = Modifier.padding(all = 16.dp),
                color = Color.White
            )
        }
    }
}
