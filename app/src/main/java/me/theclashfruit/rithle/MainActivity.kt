package me.theclashfruit.rithle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.delay
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.ui.pages.HomeScreen
import me.theclashfruit.rithle.ui.pages.LoadingScreen
import me.theclashfruit.rithle.ui.pages.NotificationsScreen
import me.theclashfruit.rithle.ui.pages.ProjectScreen
import me.theclashfruit.rithle.ui.pages.SettingsScreen
import me.theclashfruit.rithle.ui.theme.RithleTheme
import me.theclashfruit.rithle.util.TokenRepository
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

            val repo = TokenRepository(this)

            // Preload Meta (Tag)
            LaunchedEffect(true) {
                val t = repo.getAccessTokenOnce()
                if (t != null)
                    modrinth.userToken = t

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
                            navController = navController,
                            ctx = this@MainActivity
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

                    composable(
                        route = "/project/{id}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "https://modrinth.com/{type}/{id}" },
                            navDeepLink { uriPattern = "https://www.modrinth.com/{type}/{id}" },
                            navDeepLink { uriPattern = "http://modrinth.com/{type}/{id}" },
                            navDeepLink { uriPattern = "http://www.modrinth.com/{type}/{id}" }
                        ),
                        arguments = listOf(
                            navArgument("id") { nullable = false }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")!!

                        ProjectScreen(
                            navController = navController,
                            project = id
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

                        LoadingScreen()

                        LaunchedEffect(true) {
                            val token = oauth.token(code, "rithle://oauth/callback")!!

                            repo.saveToken(token.accessToken, token.expiresIn.toLong())
                            modrinth.userToken = token.accessToken

                            delay(1000.milliseconds)

                            navController.navigate(state) {
                                popUpTo("oauth/callback?code={code}&state={state}") {
                                    inclusive = true
                                }
                            }
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
