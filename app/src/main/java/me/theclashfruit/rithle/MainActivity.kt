package me.theclashfruit.rithle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.ui.theme.RithleTheme
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import me.theclashfruit.rithle.ui.pages.HomeScreen

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            val modrinth = Modrinth.getInstance()
            val oauth = modrinth.OAuth(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

            RithleTheme {
                NavHost(
                    navController = navController,
                    startDestination = "/"
                ) {
                    composable("/") {
                        HomeScreen(
                            navController = navController
                        )
                    }

                    // Authentication
                    composable(
                        route = "oauth/callback?code={code}&state={state}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "rithle://oauth/callback?code={code}&state={state}"
                            }
                        ),
                        arguments = listOf(
                            navArgument("code") { nullable = false },
                            navArgument("state") { nullable = false }
                        )
                    ) { backStackEntry ->
                        val code = backStackEntry.arguments?.getString("code")!!
                        val state = backStackEntry.arguments?.getString("state")!!

                        LaunchedEffect(true) {
                            val token = oauth.token(code, "rithle://oauth/callback")!!
                            Log.d("Token", token.toString())

                            modrinth.userToken = token.accessToken
                            navController.navigate("/")
                        }

                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Text(
                                text = "Auth code: $code; Auth state: $state",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        navController.handleDeepLink(intent)
    }
}
