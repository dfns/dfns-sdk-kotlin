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
import co.dfns.sdk.tutorial.mobile.ui.DelegatedLoginPage
import co.dfns.sdk.tutorial.mobile.ui.DelegatedRegistrationPage
import co.dfns.sdk.tutorial.mobile.ui.HomePage
import co.dfns.sdk.tutorial.mobile.ui.WalletsPage
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
        val token = remember { mutableStateOf("" )}
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