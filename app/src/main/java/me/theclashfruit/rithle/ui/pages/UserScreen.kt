package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.*
import com.composables.icons.lucide.Lucide
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.Project
import me.theclashfruit.rithle.modrinth.serializables.ProjectResult
import me.theclashfruit.rithle.modrinth.serializables.RithleUser
import me.theclashfruit.rithle.modrinth.serializables.User
import me.theclashfruit.rithle.ui.composables.ProjectCard
import me.theclashfruit.rithle.util.formatCount
import me.theclashfruit.rithle.util.timeAgo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserScreen(
    id: String?,
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var user by remember { mutableStateOf<User?>(null) }
    var rthlUser by remember { mutableStateOf<RithleUser?>(null) }
    var projects by remember { mutableStateOf<List<Project>?>(null) }

    val gridState = rememberLazyGridState()

    LaunchedEffect(modrinth.authenticated) {
        user = if (id != null)
            modrinth.user(id)
        else
            modrinth.user()

        if (user != null) {
            projects = modrinth.userProject(user!!.id).sortedByDescending { it.downloads }
            rthlUser = modrinth.rithleUser(user!!.id)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(user?.username ?: "Profile") },
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
        user?.let { currentUser ->
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 350.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(96.dp),
                            shape = RoundedCornerShape(100),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            if (!currentUser.avatarUrl.isNullOrEmpty())
                                AsyncImage(
                                    model = user!!.avatarUrl,
                                    contentDescription = "${user!!.username}'s Avatar",
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

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentUser.username,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                if (currentUser.role == "admin" || currentUser.role == "moderator") {
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                        tooltip = {
                                            PlainTooltip {
                                                Text(currentUser.role.replaceFirstChar { it.uppercase() })
                                            }
                                        },
                                        state = rememberTooltipState()
                                    ) {
                                        Icon(
                                            imageVector = if (currentUser.role == "admin") Lucide.Shield else Lucide.Section,
                                            contentDescription = currentUser.role,
                                            modifier = Modifier.padding(start = 8.dp).size(20.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                if (rthlUser != null) {
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                        tooltip = {
                                            PlainTooltip {
                                                Text("Rithle user since ${timeAgo(rthlUser!!.created)}.")
                                            }
                                        },
                                        state = rememberTooltipState()
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Wrench,
                                            contentDescription = "Rithle user since ${timeAgo(rthlUser!!.created)}.",
                                            modifier = Modifier.padding(start = 8.dp).size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    if (rthlUser!!.donated)
                                        TooltipBox(
                                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                            tooltip = {
                                                PlainTooltip {
                                                    Text("Rithle supporter.")
                                                }
                                            },
                                            state = rememberTooltipState()
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Coffee,
                                                contentDescription = "Rithle supporter.",
                                                modifier = Modifier.padding(start = 8.dp).size(20.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                }
                            }

                            currentUser.bio?.let { bio ->
                                Text(
                                    text = bio.replace("\n", " "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Start,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        UserStat(
                            icon = Lucide.Briefcase,
                            label = "Projects",
                            value = formatCount(projects?.size ?: 0)
                        )

                        UserStat(
                            icon = Lucide.Download,
                            label = "Downloads",
                            value = formatCount(projects?.sumOf { it.downloads } ?: 0)
                        )

                        UserStat(
                            icon = Lucide.Calendar,
                            label = "Joined",
                            value = timeAgo(user!!.created)
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    HorizontalDivider()
                }

                if (projects != null)
                    items(projects!!, key = { it.slug }) { project ->
                        ProjectCard(
                            showAuthor = false,
                            project = ProjectResult(
                                projectId = project.id,
                                projectType = project.projectType,
                                slug = project.slug,
                                title = project.title,
                                author = currentUser.username,
                                description = project.description,
                                categories = project.categories,
                                clientSide = project.clientSide,
                                serverSide = project.serverSide,
                                downloads = project.downloads,
                                iconUrl = project.iconUrl,
                                color = project.color,
                                threadId = project.threadId,
                                monetizationStatus = project.monetizationStatus,
                                dateCreated = project.published,
                                dateModified = project.updated,
                                displayCategories = project.categories,
                                featuredGallery = project.gallery?.firstOrNull()?.url,
                                follows = project.followers,
                                gallery = project.gallery?.map { it.url } ?: emptyList(),
                                latestVersion = project.versions?.lastOrNull(),
                                license = project.license?.name ?: "N/A",
                                versions = project.versions!!
                            ),
                            onClick = { navController.navigate("/project/${project.id}") }
                        )
                    }
                else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserStat(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement
            .spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(text = value, fontWeight = FontWeight.SemiBold)
        }
    }
}