package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Book
import com.composables.icons.lucide.Bug
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Cuboid
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Github
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Languages
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.Modrinth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()

    val extractModpacks = remember { mutableStateOf(false) }
    var secretClicks by remember { mutableStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back"
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

            if (modrinth.authenticated)
                Card() { }
            else
                Card() { }

            SettingsSection(label = "Appearance") {
                SettingsCard(
                    icon = Lucide.Moon,
                    title = "Theme",
                    subtitle = "System Default",
                    onClick = {}
                )

                SettingsCard(
                    icon = Lucide.Languages,
                    title = "Language",
                    subtitle = "System Default",
                    onClick = {}
                )
            }

            SettingsSection(label = "Modpacks") {
                SettingsCardWithSwitch(
                    icon = Lucide.Cuboid,
                    title = "Extract Modpacks",
                    subtitle = "Extract modpack contents after download",
                    enabled = extractModpacks
                )

                SettingsCard(
                    icon = Lucide.Download,
                    title = "Modpack Location",
                    subtitle = "/storage/emulated/0/downloads/Rithle",
                    onClick = {}
                )
            }

            SettingsSection(label = "About") {
                SettingsCard(
                    icon = Lucide.History,
                    title = "Version",
                    subtitle = "v${BuildConfig.VERSION_NAME} (${BuildConfig.GIT_HASH})",
                    onClick = {
                        secretClicks++
                    }
                )

                SettingsCard(
                    icon = Lucide.Book,
                    title = "Licenses",
                    onClick = {}
                )

                SettingsCardWithExternalLink(
                    icon = Lucide.Github,
                    title = "Source Code",
                    uri = "https://github.com/TheClashFruit/Rithle"
                )
            }

            if (BuildConfig.DEBUG || secretClicks > 5) {
                SettingsSection(label = "Debug") {
                    SettingsCard(
                        icon = Lucide.Bug,
                        title = "Export Debug Logs",
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
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(uri) }
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