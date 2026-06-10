package me.theclashfruit.rithle.ui.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleWarning
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.User
import com.composables.icons.lucide.X
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.Category
import me.theclashfruit.rithle.modrinth.serializables.GameVersion
import me.theclashfruit.rithle.modrinth.serializables.Loader
import me.theclashfruit.rithle.modrinth.serializables.ProjectResult
import me.theclashfruit.rithle.modrinth.serializables.Search
import me.theclashfruit.rithle.ui.composables.FilterBottomSheetWithIcons
import me.theclashfruit.rithle.ui.composables.GameVersionFilterBottomSheet
import me.theclashfruit.rithle.ui.composables.ProjectCard
import me.theclashfruit.rithle.util.Facet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()
    val locale = LocalLocale.current.platformLocale

    var categories by remember { mutableStateOf<List<Category>>(listOf()) }
    var loaders by remember { mutableStateOf<List<Loader>>(listOf()) }
    LaunchedEffect(true) {
        categories = modrinth.categories().map { item ->
            item.copy(
                name = item.name
                    .split("-")
                    .joinToString(" ") { i ->
                        i.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                        }
                    }
            )
        }

        loaders = modrinth.loaders()
    }

    val uriHandler = LocalUriHandler.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mods", "Resource Packs", "Data Packs", "Modpacks", "Shaders", "Plugins")
    val currentType = when (selectedTabIndex) {
        0 -> "mod"
        1 -> "resourcepack"
        2 -> "mod" // datapacks are mods on modrinth
        3 -> "modpack"
        4 -> "shader"
        5 -> "mod" // plugins are mods on modrinth
        else -> "n/a"
    }

    var isAccountMenuExpanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search ${tabs[selectedTabIndex]}...")
                },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded)
                        IconButton(
                            onClick = {
                                scope.launch { searchBarState.animateToCollapsed() }
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    else
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search"
                        )
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty())
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear"
                            )
                        }
                }
            )
        }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            delay(100)
            isRefreshing = false
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = { onRefresh() },
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                AppBarWithSearch(
                    scrollBehavior = scrollBehavior,
                    state = searchBarState,
                    colors = appBarWithSearchColors,
                    inputField = inputField,
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate("/notifications")
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.Bell,
                                contentDescription = "Notifications"
                            )
                        }

                        IconButton(
                            onClick = {
                                isAccountMenuExpanded = true
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.User,
                                contentDescription = "Account"
                            )

                            DropdownMenu(
                                expanded = isAccountMenuExpanded,
                                onDismissRequest = { isAccountMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Profile") },
                                    leadingIcon = { Icon(Lucide.User, contentDescription = null) },
                                    onClick = {
                                        isAccountMenuExpanded = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = { Icon(Lucide.Settings, contentDescription = null) },
                                    onClick = {
                                        isAccountMenuExpanded = false
                                    }
                                )

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = { Text("About") },
                                    leadingIcon = { Icon(Lucide.Info, contentDescription = null) },
                                    onClick = { /* Do something... */ }
                                )

                                DropdownMenuItem(
                                    text = { Text("Report Issues") },
                                    leadingIcon = { Icon(Lucide.MessageCircleWarning, contentDescription = null) },
                                    trailingIcon = { Icon(Lucide.ExternalLink, contentDescription = null) },
                                    onClick = {
                                        uriHandler.openUri("https://github.com/TheClashFruit/Rithle/issues")
                                    }
                                )


                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = { Text("Log Out") },
                                    leadingIcon = { Icon(Lucide.LogOut, contentDescription = null) },
                                    onClick = {
                                        isAccountMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                )

                ExpandedFullScreenContainedSearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    colors = appBarWithSearchColors.searchBarColors,
                ) {
                    var showGameVersionsBottomSheet by remember { mutableStateOf(false) }
                    var showLoadersBottomSheet by remember { mutableStateOf(false) }

                    var openSource by remember { mutableStateOf(false) }

                    var gameVersions by remember { mutableStateOf<List<GameVersion>>(listOf()) }

                    // filter stuff out
                    val filteredCategories by remember(categories, currentType) {
                        derivedStateOf {
                            categories
                                .filter { it.projectType == currentType }
                                .groupBy { it.header }
                        }
                    }

                    val filteredLoaders by remember(loaders, currentType, selectedTabIndex) {
                        derivedStateOf {
                            loaders
                                .filter {
                                    it.supportedProjectTypes.contains(if (selectedTabIndex == 5) "plugin" else if (selectedTabIndex == 2) "datapack" else currentType)
                                }
                        }
                    }

                    // Filter Chips
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 4.dp)
                    ) {
                        // Game versiom, same for all.
                        FilterChip(
                            selected = gameVersions.isNotEmpty(),
                            modifier =
                                Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { showGameVersionsBottomSheet = true },
                            label = {
                                Text(
                                    if (gameVersions.size > 1) "${gameVersions[0].version} +${gameVersions.size - 1}"
                                    else if (gameVersions.size == 1) gameVersions[0].version
                                    else "Game Version"
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Open",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        )

                        GameVersionFilterBottomSheet(
                            show = showGameVersionsBottomSheet,
                            onDismiss = { selection ->
                                gameVersions = selection
                                showGameVersionsBottomSheet = false
                            }
                        )

                        if (filteredLoaders.isNotEmpty()) {
                            FilterChip(
                                selected = false,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 4.dp)
                                        .align(alignment = Alignment.CenterVertically),
                                onClick = { showLoadersBottomSheet = true },
                                label = {
                                    Text(
                                        "Loader"
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Lucide.ChevronDown,
                                        contentDescription = "Open",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                    )
                                }
                            )

                            FilterBottomSheetWithIcons(
                                title = "Loader",
                                show = showLoadersBottomSheet,
                                items = filteredLoaders,
                                icon = { it.icon },
                                label = { it.name },
                                onDismiss = { showLoadersBottomSheet = false }
                            )
                        }

                        filteredCategories.forEach { (header, items) ->
                            var active by remember { mutableStateOf(false) }
                            var open by remember { mutableStateOf(false) }

                            val title = header.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

                            FilterChip(
                                selected = active,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 4.dp)
                                        .align(alignment = Alignment.CenterVertically),
                                onClick = { open = true },
                                label = { Text(title) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Lucide.ChevronDown,
                                        contentDescription = "Open",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                    )
                                }
                            )

                            FilterBottomSheetWithIcons(
                                title = title,
                                show = open,
                                items = items,
                                icon = { it.icon },
                                label = { it.name },
                                onDismiss = { open = false }
                            )
                        }

                        // Is opensource, same for all
                        FilterChip(
                            selected = openSource,
                            modifier =
                                Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { openSource = !openSource },
                            label = { Text("Open Source") }
                        )
                    }

                    // The search!

                    Text(
                        text = "HELLOW ORLD: ${textFieldState.text}"
                    )
                }

                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title) }
                        )
                    }
                }
            }

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            var projects by remember { mutableStateOf<List<ProjectResult>>(emptyList()) }
            var offset by remember { mutableIntStateOf(0) }
            var isLoading by remember { mutableStateOf(false) }
            var hasMore by remember { mutableStateOf(true) }
            val limit = 20
            val coroutineScope = rememberCoroutineScope()

            val loadMore: () -> Unit = remember {
                {
                    if (!isLoading && hasMore) {
                        coroutineScope.launch {
                            isLoading = true
                            val result = modrinth.search(
                                facets = Facet
                                    .builder()
                                    .and(Facet.ProjectType, "mod")
                                    .build(),
                                offset = offset,
                                limit = limit
                            )
                            projects = projects + result.hits
                            offset += limit
                            hasMore = offset < result.totalHits
                            isLoading = false
                        }
                    }
                }
            }

            LaunchedEffect(true) {
                loadMore()
            }

            ProjectCardList(
                projects = projects,
                isLoading = isLoading,
                onEndReached = { coroutineScope.launch { loadMore() } }
            )

            PullToRefreshDefaults.Indicator(
                state = refreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ProjectCardList(
    projects: List<ProjectResult>,
    onEndReached: () -> Unit,
    isLoading: Boolean,
) {
    val listState = rememberLazyListState()

    // Trigger when 3 items from the end
    val endReached by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            last != null && last.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(endReached) {
        if (endReached) onEndReached()
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(projects, key = { it.slug }) { project ->
            ProjectCard(project = project, onClick = {})
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}