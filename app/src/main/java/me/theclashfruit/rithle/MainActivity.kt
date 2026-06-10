package me.theclashfruit.rithle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.ui.theme.RithleTheme
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.ui.pages.HomeScreen
import me.theclashfruit.rithle.ui.pages.LoadingScreen
import me.theclashfruit.rithle.ui.pages.NotificationsScreen
import me.theclashfruit.rithle.ui.pages.SettingsScreen
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            val modrinth = Modrinth.getInstance()
            val oauth = modrinth.OAuth(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

            // Preload Meta (Tag)
            LaunchedEffect(true) {
                modrinth.gameVersions()
                modrinth.loaders()
                modrinth.categories()
            }

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

                    composable("/notifications") {
                        NotificationsScreen(
                            navController = navController
                        )
                    }

                    composable("/settings") {
                        SettingsScreen(
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

                            delay(1000.milliseconds)

                            modrinth.userToken = token.accessToken
                            navController.navigate("/") {
                                popUpTo("oauth/callback?code={code}&state={state}") {
                                    inclusive = true
                                }
                            }
                        }

                        LoadingScreen()
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
