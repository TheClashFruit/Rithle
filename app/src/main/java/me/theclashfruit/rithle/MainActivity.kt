package me.theclashfruit.rithle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mikepenz.markdown.m3.Markdown
import kotlinx.coroutines.delay
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.ui.pages.HomeScreen
import me.theclashfruit.rithle.ui.pages.LoadingScreen
import me.theclashfruit.rithle.ui.pages.NotificationsScreen
import me.theclashfruit.rithle.ui.pages.ProjectScreen
import me.theclashfruit.rithle.ui.pages.SettingsScreen
import me.theclashfruit.rithle.ui.pages.UserScreen
import me.theclashfruit.rithle.ui.theme.RithleTheme
import me.theclashfruit.rithle.util.Settings
import me.theclashfruit.rithle.util.SettingsStore
import me.theclashfruit.rithle.util.TokenRepository
import me.theclashfruit.rithle.util.UpdateChecker
import me.theclashfruit.rithle.util.launchCustomTabs
import me.theclashfruit.rithle.util.serializables.GitHubRelease
import me.theclashfruit.rithle.util.settingsStore
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
                if (t != null) {
                    modrinth.userToken = t

                    // mandatory for keeping the badges.
                    modrinth.analytics.trackLogin()
                }

                modrinth.gameVersions()
                modrinth.loaders()
                modrinth.categories()
            }

            val settingsStore = SettingsStore(this)
            val settingsState by settingsStore.settingsFlow.collectAsStateWithLifecycle(initialValue = Settings())

            val darkTheme = when (settingsState.theme) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            val check = settingsState.updateChecker

            var update by remember { mutableStateOf<GitHubRelease?>(null) }
            val showDialog = remember { mutableStateOf(false) }

            LaunchedEffect(check) {
                if (check) {
                    val d = UpdateChecker.checkUpdate()

                    if (d.hasUpdate) {
                        update = d.data
                        showDialog.value = true
                    }
                }
            }

            RithleTheme(darkTheme) {
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
                        route = "/user?id={id}",
                        arguments = listOf(
                            navArgument("id") { nullable = true }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")

                        UserScreen(id, navController)
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
                            if (BuildConfig.API_MODRINTH_LOCAL_OAUTH) {
                                val token = oauth.token(code, "rithle://oauth/callback")!!

                                repo.saveToken(token.accessToken, token.expiresIn.toLong())
                                modrinth.userToken = token.accessToken
                            } else {
                                val token = oauth.token(code)!!

                                repo.saveToken(token.accessToken, token.expiresIn.toLong())
                                modrinth.userToken = token.accessToken
                            }

                            delay(1000.milliseconds)

                            navController.navigate(state) {
                                popUpTo("oauth/callback?code=$code&state=$state") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }

                when { showDialog.value ->
                    if (update != null) {
                        AlertDialog(
                            title = { Text(stringResource(R.string.update)) },
                            text = {
                                Text(
                                    text = update!!.body,
                                )
                            },
                            onDismissRequest = {
                                showDialog.value = false
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDialog.value = false
                                    }
                                ) {
                                    Text("Not Now")
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog.value = false

                                        this.launchCustomTabs(update!!.assets.first { it.contentType == "application/vnd.android.package-archive" || it.name.endsWith(".apk") }.browserDownloadUrl)
                                    }
                                ) {
                                    Text("Update")
                                }
                            }
                        )
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
