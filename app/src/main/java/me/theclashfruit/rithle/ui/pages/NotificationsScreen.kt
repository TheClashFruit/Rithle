package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.Notification
import me.theclashfruit.rithle.modrinth.serializables.Project
import me.theclashfruit.rithle.modrinth.serializables.User
import me.theclashfruit.rithle.modrinth.serializables.Version

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsScreen(
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()

    var user by remember { mutableStateOf<User?>(null) }
    var notifications by remember { mutableStateOf<List<Notification>?>(null) }

    var projects by remember { mutableStateOf<List<Project>?>(null) }
    var versions by remember { mutableStateOf<List<Version>?>(null) }

    val idRegex = remember { """/project/(?<projectId>[^/]+)(?:/version/(?<versionId>[^/]+))?""".toRegex() }

    LaunchedEffect(modrinth.authenticated) {
        user = modrinth.user()

        if (user != null) {
            val tmpNotifs = modrinth.userNotifications(user!!.id).filter { !it.read }

            val extractedProjectIds = mutableListOf<String?>()
            val extractedVersionIds = mutableListOf<String?>()

            for (notification in tmpNotifs) {
                val matchResult = idRegex.find(notification.link)

                // Collect the IDs into their respective lists
                extractedProjectIds.add(matchResult?.groups["projectId"]?.value)
                extractedVersionIds.add(matchResult?.groups["versionId"]?.value)
            }

            val prj = modrinth.projects(extractedProjectIds.filterNotNull())
            val vrs = modrinth.versions(extractedVersionIds.filterNotNull())

            tmpNotifs.forEach { notif ->
                var updatedText = notif.text

                prj.forEach { project ->
                    updatedText = updatedText.replace(project.id, project.title)
                }

                vrs.forEach { version ->
                    updatedText = updatedText.replace(version.id, version.name)
                }

                notif.text = updatedText
            }

            projects = prj
            versions = vrs
            notifications = tmpNotifs
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.notifications_title)) },
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
        if (notifications != null) {
            if (notifications!!.isNotEmpty())
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(notifications!!, key = { it.id }) { notification ->
                        ListItem(
                            onClick = { navController.navigate(notification.link.replace(Regex("""/version/.*"""), "")) },
                            supportingContent = { Text(notification.text)  }
                        ) {
                            Text(notification.title)
                        }
                    }
                }
            else
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_notifications_yet))
                }
        } else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
    }
}