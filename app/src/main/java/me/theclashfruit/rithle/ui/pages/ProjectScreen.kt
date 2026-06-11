package me.theclashfruit.rithle.ui.pages

import android.R.attr.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Box
import com.composables.icons.lucide.Lucide
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.ReferenceLinkHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.modrinth.Modrinth
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

    Surface(
        modifier = Modifier
            .size(80.dp)
            .clip(CardDefaults.shape),
        color = if (data.iconUrl != null) CardDefaults.cardColors().containerColor else color
    ) {
        if (data!!.iconUrl != null)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .verticalScroll(rememberScrollState())
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
                content = data.body,
                imageTransformer = Coil3ImageTransformerImpl
            )
        }
    }
}

@Composable
fun GalleryPage(
    data: Project
) {
    Text("Gallery")
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