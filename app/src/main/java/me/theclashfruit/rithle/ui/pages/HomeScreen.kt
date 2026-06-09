package me.theclashfruit.rithle.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Axe
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Box
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleWarning
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.User
import com.composables.icons.lucide.X
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.ui.composables.ProjectCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val uriHandler = LocalUriHandler.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mods", "Resource Packs", "Data Packs", "Modpacks", "Shaders", "Plugins")

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
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = {
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
            delay(5000)
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
                    var selected by remember { mutableStateOf(false) }
                    val colorNames =
                        listOf(
                            "Blue",
                            "Yellow",
                            "Red",
                            "Orange",
                            "Black",
                            "Green",
                            "White",
                            "Magenta",
                            "Gray",
                            "Transparent",
                        )


                    // Filter Chips
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()) // 👈 This makes it scrollable
                            .padding(horizontal = 4.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            modifier =
                                Modifier.padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { /* TODO: Show dialog */ },
                            label = { Text("Game Version") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Open",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        )

                        FilterChip(
                            selected = false,
                            modifier =
                                Modifier.padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { /* TODO: Show dialog */ },
                            label = { Text("Loader") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Open",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        )

                        FilterChip(
                            selected = false,
                            modifier =
                                Modifier.padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { /* TODO: Show dialog */ },
                            label = { Text("Category") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Open",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        )

                        FilterChip(
                            selected = false,
                            modifier =
                                Modifier.padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { /* TODO: Show dialog */ },
                            label = { Text("Environment") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Open",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        )

                        FilterChip(
                            selected = selected,
                            modifier =
                                Modifier.padding(horizontal = 4.dp)
                                    .align(alignment = Alignment.CenterVertically),
                            onClick = { selected = !selected },
                            label = { Text("Open Source") }
                        )
                    }

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
            Column(modifier = Modifier.fillMaxSize()) {
                ScrollableTabContent(30)
            }

            PullToRefreshDefaults.Indicator(
                state = refreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ScrollableTabContent(count: Int) {
    val itemsList = remember(count) { List(count) { "Item #$it" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemsList) { itemText ->
            ProjectCard(
                title = itemText,
                author = "John",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur et nisi justo. Duis eget euismod ex.",
                onClick = {}
            )
        }
    }
}
