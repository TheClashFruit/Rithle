package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Book
import com.composables.icons.lucide.Bug
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Coffee
import com.composables.icons.lucide.Cuboid
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Github
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Languages
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.User
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.modrinth.serializables.User
import me.theclashfruit.rithle.util.launchCustomTabs
import me.theclashfruit.rithle.util.timeAgo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()
    val oauth = modrinth.OAuth(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

    val extractModpacks = remember { mutableStateOf(false) }
    var secretClicks by remember { mutableIntStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val ctx = LocalContext.current

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // TODO: Account section

            if (modrinth.authenticated) {
                var user by remember { mutableStateOf<User?>(null) }

                LaunchedEffect(modrinth.authenticated) {
                    user = modrinth.user()
                }

                if (user != null)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("/user")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(100),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                if (user!!.avatarUrl.isNotEmpty())
                                    AsyncImage(
                                        model = user!!.avatarUrl,
                                        contentDescription = stringResource(R.string.user_avatar_description, user!!.username),
                                        modifier = Modifier.fillMaxSize()
                                    )
                                else
                                    Icon(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(12.dp),
                                        imageVector = Lucide.User,
                                        contentDescription = null
                                    )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user!!.username,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = stringResource(R.string.joined_date, timeAgo(user!!.created)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Lucide.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
            } else
                SettingsCard(
                    icon = Lucide.LogIn,
                    title = stringResource(R.string.not_logged_in),
                    subtitle = stringResource(R.string.login_with_modrinth),
                    onClick = {
                        val url = oauth.authorizationUrl(if (BuildConfig.API_MODRINTH_LOCAL_OAUTH) "rithle://oauth/callback" else "${BuildConfig.API_RITHLE}/oauth/callback", Scope.entries, "app:/settings")
                        ctx.launchCustomTabs(url)
                    }
                )

            SettingsSection(label = stringResource(R.string.appearance)) {
                SettingsCard(
                    icon = Lucide.Moon,
                    title = stringResource(R.string.theme),
                    subtitle = stringResource(R.string.system_default),
                    onClick = {}
                )

                SettingsCard(
                    icon = Lucide.Languages,
                    title = stringResource(R.string.language),
                    subtitle = stringResource(R.string.system_default),
                    onClick = {}
                )
            }

            SettingsSection(label = stringResource(R.string.modpacks)) {
                SettingsCardWithSwitch(
                    icon = Lucide.Cuboid,
                    title = stringResource(R.string.extract_modpacks),
                    subtitle = stringResource(R.string.extract_modpacks_subtitle),
                    enabled = extractModpacks
                )

                SettingsCard(
                    icon = Lucide.Download,
                    title = stringResource(R.string.modpack_location),
                    subtitle = stringResource(R.string.default_modpack_location),
                    onClick = {}
                )
            }

            SettingsSection(label = stringResource(R.string.about)) {
                SettingsCard(
                    icon = Lucide.History,
                    title = stringResource(R.string.version),
                    subtitle = stringResource(R.string.version_subtitle, BuildConfig.VERSION_NAME, BuildConfig.GIT_HASH),
                    onClick = {
                        secretClicks++
                    }
                )

                SettingsCard(
                    icon = Lucide.Book,
                    title = stringResource(R.string.licenses),
                    onClick = {}
                )

                SettingsCardWithExternalLink(
                    icon = Lucide.Github,
                    title = stringResource(R.string.source_code),
                    uri = "https://github.com/TheClashFruit/Rithle"
                )

                SettingsCardWithExternalLink(
                    icon = Lucide.Coffee,
                    title = stringResource(R.string.support_project),
                    uri = "https://ko-fi.com/TheClashFruit"
                )
            }

            if (BuildConfig.DEBUG || secretClicks > 5) {
                SettingsSection(label = stringResource(R.string.debug)) {
                    SettingsCard(
                        icon = Lucide.Bug,
                        title = stringResource(R.string.export_debug_logs),
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        content()
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(icon = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCardWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    enabled: MutableState<Boolean>
) {
    var checked by enabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked = !checked }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(icon = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }
    }
}

@Composable
fun SettingsCardWithExternalLink(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    uri: String
) {
    val ctx = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { ctx.launchCustomTabs(uri) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(icon = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Lucide.ExternalLink,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsIcon(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(100),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .padding(12.dp),
            imageVector = icon,
            contentDescription = null
        )
    }
}