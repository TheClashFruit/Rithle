package me.theclashfruit.rithle.ui.pages

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleWarning
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.User
import com.composables.icons.lucide.X
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.modrinth.serializables.Category
import me.theclashfruit.rithle.modrinth.serializables.GameVersion
import me.theclashfruit.rithle.modrinth.serializables.Loader
import me.theclashfruit.rithle.ui.composables.FilterBottomSheetWithIcons
import me.theclashfruit.rithle.ui.composables.GameVersionFilterBottomSheet
import me.theclashfruit.rithle.ui.composables.ProjectCardList
import me.theclashfruit.rithle.util.Facet
import me.theclashfruit.rithle.util.TokenRepository
import me.theclashfruit.rithle.util.launchCustomTabs
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    ctx: Context
) {
    val modrinth = Modrinth.getInstance()
    val oauth = modrinth.OAuth(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

    val repo = TokenRepository(ctx)

    val locale = LocalLocale.current.platformLocale
    val coroutineScope = rememberCoroutineScope()

    var categories by remember { mutableStateOf<List<Category>>(listOf()) }
    var loaders by remember { mutableStateOf<List<Loader>>(listOf()) }
    LaunchedEffect(true) {
        categories = modrinth.categories()
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

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
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
                                if (modrinth.authenticated)
                                    DropdownMenuItem(
                                        text = { Text("Profile") },
                                        leadingIcon = { Icon(Lucide.User, contentDescription = null) },
                                        onClick = {
                                            isAccountMenuExpanded = false

                                            navController.navigate("/user")
                                        }
                                    )
                                else
                                    DropdownMenuItem(
                                        text = { Text("Login") },
                                        leadingIcon = { Icon(Lucide.LogIn, contentDescription = null) },
                                        onClick = {
                                            isAccountMenuExpanded = false

                                            val url = oauth.authorizationUrl("rithle://oauth/callback", Scope.entries, "/")
                                            ctx.launchCustomTabs(url)
                                        }
                                    )

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = { Icon(Lucide.Settings, contentDescription = null) },
                                    onClick = {
                                        isAccountMenuExpanded = false

                                        navController.navigate("/settings")
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Report Issues") },
                                    leadingIcon = { Icon(Lucide.MessageCircleWarning, contentDescription = null) },
                                    trailingIcon = { Icon(Lucide.ExternalLink, contentDescription = null) },
                                    onClick = {
                                        uriHandler.openUri("https://github.com/TheClashFruit/Rithle/issues")

                                        isAccountMenuExpanded = false
                                    }
                                )

                                if (modrinth.authenticated) {
                                    HorizontalDivider()

                                    DropdownMenuItem(
                                        text = { Text("Log Out") },
                                        leadingIcon = { Icon(Lucide.LogOut, contentDescription = null) },
                                        onClick = {
                                            scope.launch {
                                                repo.clear()
                                                modrinth.userToken = null
                                            }

                                            isAccountMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )

                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex
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

            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                var showGameVersionsBottomSheet by remember { mutableStateOf(false) }
                var showLoadersBottomSheet by remember { mutableStateOf(false) }

                var gameVersions by remember { mutableStateOf<List<GameVersion>>(listOf()) }
                var selectedLoaders by remember { mutableStateOf<List<Loader>>(listOf()) }
                var selectedCategories by remember { mutableStateOf<List<Category>>(listOf()) }
                var openSource by remember { mutableStateOf(false) }

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
                        selection = gameVersions,
                        onChange = { selection ->
                            gameVersions = selection
                        },
                        onDismiss = {
                            showGameVersionsBottomSheet = false
                        }
                    )

                    if (filteredLoaders.isNotEmpty() && filteredLoaders.size > 1) {
                        FilterChip(
                            selected = selectedLoaders.isNotEmpty(),
                            modifier =
                                Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { showLoadersBottomSheet = true },
                            label = {
                                Text(
                                    if (selectedLoaders.size > 1) "${selectedLoaders[0].name} +${selectedLoaders.size - 1}"
                                    else if (selectedLoaders.size == 1) selectedLoaders[0].name
                                    else "Loader"
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
                            selection = selectedLoaders,
                            icon = { it.icon },
                            label = { it.name },
                            onChange = { selectedLoaders = it },
                            onDismiss = { showLoadersBottomSheet = false },
                        )
                    }

                    filteredCategories.forEach { (header, items) ->
                        var open by remember { mutableStateOf(false) }
                        var selection by remember { mutableStateOf<List<Category>>(listOf()) }

                        LaunchedEffect(selection) {
                            selectedCategories = (selectedCategories - items.toSet()) + selection
                        }

                        val title = header.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

                        FilterChip(
                            selected = selection.isNotEmpty(),
                            modifier =
                                Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { open = true },
                            label = { Text(
                                if (selection.size > 1) "${selection[0].name} +${selection.size - 1}"
                                else if (selection.size == 1) selection[0].name
                                else title
                            ) },
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
                            selection = selection,
                            icon = { it.icon },
                            label = { it.name },
                            onChange = { selection = it },
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
                ProjectCardList(
                    navController = navController,
                    query = if (textFieldState.text.isNotEmpty()) textFieldState.text.toString() else null,
                    facets = Facet
                        .builder()
                        .and(Facet.ProjectType, when (selectedTabIndex) {
                            0 -> "mod"
                            1 -> "resourcepack"
                            2 -> "datapack"
                            3 -> "modpack"
                            4 -> "shader"
                            5 -> "plugin"
                            else -> "n/a"
                        })
                        .apply {
                            if (selectedCategories.isNotEmpty())
                                or(Facet.Category, *selectedCategories.map { it.name }.toTypedArray())
                            if (selectedLoaders.isNotEmpty())
                                or(Facet.Category, *selectedLoaders.map { it.name }.toTypedArray())

                            if (openSource)
                                and(Facet.OpenSource, true)
                        },
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            ProjectCardList(
                navController = navController,
                facets = Facet
                    .builder()
                    .and(Facet.ProjectType, when (page) {
                        0 -> "mod"
                        1 -> "resourcepack"
                        2 -> "datapack"
                        3 -> "modpack"
                        4 -> "shader"
                        5 -> "plugin"
                        else -> "n/a"
                    }),
            )
        }
    }
}
