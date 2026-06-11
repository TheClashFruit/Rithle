package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Clipboard
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Flag
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Ribbon
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.LazyMarkdownSuccess
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.Gallery
import me.theclashfruit.rithle.modrinth.serializables.Project
import me.theclashfruit.rithle.util.launchCustomTabs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectScreen(
    navController: NavHostController,
    project: String
) {
    val modrinth = Modrinth.getInstance();
    var data by remember { mutableStateOf<Project?>(null) }

    LaunchedEffect(project) {
        data = modrinth.project(project)
    }

    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = mutableListOf<String>()

    tabs.add("Description")
    if (data != null && data!!.gallery?.isNotEmpty() == true)
        tabs.add("Gallery")
    tabs.add("Changelog")
    tabs.add("Versions")

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    val tabRowBgColor = lerp(
        start = TopAppBarDefaults.topAppBarColors().containerColor,
        stop = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
        fraction = scrollBehavior.state.collapsedFraction
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeFlexibleTopAppBar(
                    title = { Text("Project") },
                    subtitle = { if (data != null) Text(data!!.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        AppBarRow(maxItemCount = 4) {
                            clickableItem(
                                label = "Download",
                                icon = { Icon(Lucide.Download, contentDescription = "Download") },
                                onClick = { /* Handle Download */ }
                            )
                            clickableItem(
                                label = "Follow",
                                icon = { Icon(Lucide.Heart, contentDescription = "Follow") },
                                onClick = { /* Handle Follow */ }
                            )
                            clickableItem(
                                label = "Save",
                                icon = { Icon(Lucide.Bookmark, contentDescription = "Save") },
                                onClick = { /* Handle Save */ }
                            )
                            clickableItem(
                                label = "Report",
                                icon = { Icon(Lucide.Flag, contentDescription = "Report") },
                                onClick = { /* Handle Report */ }
                            )
                            clickableItem(
                                label = "Copy ID",
                                icon = { Icon(Lucide.Clipboard, contentDescription = "Copy ID") },
                                onClick = { /* Handle Copy ID */ }
                            )
                            clickableItem(
                                label = "Copy permanent link",
                                icon = { Icon(Lucide.Clipboard, contentDescription = "Copy permanent link") },
                                onClick = { /* Handle Copy Link */ }
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = tabRowBgColor,
                    divider = {
                        if (scrollBehavior.state.collapsedFraction == 0f) {
                            HorizontalDivider()
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index

                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (data == null)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        else
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                if (tabs.size == 3)
                    when (page) {
                        0 -> DescriptionPage(data!!, navController)
                        1 -> ChangelogPage(data!!)
                        2 -> VersionsPage(data!!)
                    }

                if (tabs.size == 4 && tabs.contains("Gallery"))
                    when (page) {
                        0 -> DescriptionPage(data!!, navController)
                        1 -> GalleryPage(data!!)
                        2 -> ChangelogPage(data!!)
                        3 -> VersionsPage(data!!)
                    }

                if (tabs.size == 4 && !tabs.contains("Gallery"))
                    when (page) {
                        0 -> DescriptionPage(data!!, navController)
                        1 -> ChangelogPage(data!!)
                        2 -> VersionsPage(data!!)
                        3 -> ModerationPage(data!!)
                    }

                if (tabs.size == 5)
                    when (page) {
                        0 -> DescriptionPage(data!!, navController)
                        1 -> GalleryPage(data!!)
                        2 -> ChangelogPage(data!!)
                        3 -> VersionsPage(data!!)
                        4 -> ModerationPage(data!!)
                    }
            }
    }
}

@Composable
fun DescriptionPage(
    data: Project,
    navController: NavHostController
) {
    val ctx = LocalContext.current

    val color = if (data.color != null)
        Color((data.color ?: 0) or 0xFF000000.toInt())
    else
        MaterialTheme.colorScheme.primaryContainer

    /*
    Surface(
        modifier = Modifier
            .size(80.dp)
            .clip(CardDefaults.shape),
        color = if (data.iconUrl != null) CardDefaults.cardColors().containerColor else color
    ) {
        if (data.iconUrl != null)
            AsyncImage(
                model = data.iconUrl,
                contentDescription = "${data.title}'s Icon",
                modifier = Modifier.fillMaxSize()
            )
        else
            Icon(
                imageVector = Lucide.Box,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
    }
    */

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CompositionLocalProvider(
            LocalUriHandler provides object : UriHandler {
                override fun openUri(url: String) {
                    val uri = url.toUri()

                    if (uri.host == "modrinth.com")
                        if (uri.pathSegments.first().contains(Regex("""/(mod|modpack|resourcepack|datapack|shader|plugin|project)/.*""")))
                            navController.navigate("/project/${uri.pathSegments.last()}")

                    ctx.launchCustomTabs(url)
                }
            }
        ) {
            Markdown(
                modifier = Modifier.fillMaxSize(),
                content = data.body,
                imageTransformer = Coil3ImageTransformerImpl,
                success = { state, components, modifier ->
                    LazyMarkdownSuccess(state, components, modifier, contentPadding = PaddingValues(16.dp))
                },
                typography = markdownTypography(
                    h1 = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    ),
                    h2 = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 36.sp
                    ),
                    h3 = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp
                    ),
                    h4 = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    ),
                    h5 = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                    h6 = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    text = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        letterSpacing = 0.25.sp
                    ),
                    paragraph = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp
                    ),
                    code = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    ),
                    inlineCode = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    quote = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textLink = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            )
        }
    }
}

@Composable
fun GalleryPage(
    data: Project,
    modifier: Modifier = Modifier
) {
    val galleryItems = data.gallery?.sortedBy { it.ordering } ?: emptyList()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (galleryItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No images available for this project.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(galleryItems, key = { it.url }) { item ->
                    GalleryItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun GalleryItemCard(
    item: Gallery,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = item.url,
                contentDescription = item.title ?: "Gallery Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }
    }
}

@Composable
fun ChangelogPage(
    data: Project
) {
    Text("Changelog")
}

@Composable
fun VersionsPage(
    data: Project
) {
    Text("Versions")
}

@Composable
fun ModerationPage(
    data: Project
) {
    Text("Moderation")
}