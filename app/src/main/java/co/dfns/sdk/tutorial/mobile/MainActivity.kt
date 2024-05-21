package co.dfns.sdk.tutorial.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.dfns.sdk.tutorial.mobile.ui.theme.DfnsAndroidSDKExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DfnsAndroidSDKExampleTheme {
                ScreenMain()
            }
        }
    }

    @Composable
    fun ScreenMain() {
        val navController = rememberNavController()

        val username = remember { mutableStateOf("") }
        val token =
            remember { mutableStateOf("eyJ0eXAiOiJKV1QiLCJhbGciOiJFZERTQSJ9.eyJpc3MiOiJhdXRoLmRmbnMud3RmIiwiYXVkIjoiZGZuczphdXRoOnVzZXIiLCJzdWIiOiJvci15YW5rZS1tYXJzLTZ1bG9mYW1vZ2c4NHM4N3YiLCJqdGkiOiJ1ai0ydDN0NS12dWdlcy05bWlhYjM3ZDc4NjN0b3R0Iiwic2NvcGUiOiIiLCJwZXJtaXNzaW9ucyI6WyJXYWxsZXRzOlJlYWQiLCJXYWxsZXRzOkdlbmVyYXRlU2lnbmF0dXJlIiwiV2FsbGV0czpSZWFkU2lnbmF0dXJlIiwiV2FsbGV0czpSZWFkVHJhbnNhY3Rpb24iLCJXYWxsZXRzOlJlYWRUcmFuc2ZlciIsIldhbGxldHM6VHJhbnNmZXJBc3NldCIsIldhbGxldHM6VXBkYXRlIiwiV2FsbGV0czpCcm9hZGNhc3RUcmFuc2FjdGlvbiIsIldhbGxldHM6Q3JlYXRlIl0sImh0dHBzOi8vY3VzdG9tL3VzZXJuYW1lIjoiYW5kcm9pZEBkZm5zLmNvIiwiaHR0cHM6Ly9jdXN0b20vYXBwX21ldGFkYXRhIjp7InVzZXJJZCI6InVzLTFxdG84LW1uNmZmLThiaWF1cDRxZHMwOG1wM2YiLCJvcmdJZCI6Im9yLXlhbmtlLW1hcnMtNnVsb2ZhbW9nZzg0czg3diIsInRva2VuS2luZCI6IlRva2VuIn0sImlhdCI6MTcxNjIyNzc1MywiZXhwIjoxNzE2MjQ5MzUzfQ.NpJh0jL7GETyw5TWVhEJLI2YV_Du42PKWzm2k7R-VhMJbsgMbp8IKlDigmSk8NFs5GWsPTtFYuxl93TJcd2IDg") }

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomePage(
                    onDelegatedRegistration = {
                        navController.navigate("delegated-registration")
                    },
                    onDelegatedLogin = {
                        navController.navigate("delegated-login")
                    },
                    onWallets = { navController.navigate("wallets") },
                    token,
                )
            }
            composable(
                "delegated-registration",
            ) {
                DelegatedRegistrationPage(
                    activity = this@MainActivity, username
                )
            }
            composable(
                "delegated-login",
            ) {
                DelegatedLoginPage(
                    token, username
                )
            }
            composable(
                "wallets",
            ) {
                WalletsPage(
                    activity = this@MainActivity, token
                )
            }
        }
    }
}