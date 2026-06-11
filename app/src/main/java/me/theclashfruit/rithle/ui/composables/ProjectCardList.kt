package me.theclashfruit.rithle.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.ProjectResult
import me.theclashfruit.rithle.util.Facet

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectCardList(
    query: String? = null,
    facets: Facet.Builder,
    limit: Int = 20,
    navController: NavHostController
) {
    val modrinth = Modrinth.getInstance()

    var projects by remember { mutableStateOf<List<ProjectResult>>(emptyList()) }
    var offset by remember { mutableIntStateOf(0) }

    var isLoading by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Trigger when 3 items from the end
    val endReached by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            last != null && last.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    fun loadMore() {
        if (isLoading || !hasMore) return
        coroutineScope.launch {
            isLoading = true
            val result = modrinth.search(
                facets = facets.build(),
                offset = offset,
                limit = limit,
                query = query
            )
            projects = projects + result.hits
            offset += limit
            hasMore = offset < result.totalHits
            isLoading = false
        }
    }

    LaunchedEffect(facets, query) {
        projects = emptyList()
        offset = 0
        hasMore = true
        isLoading = false

        // call inline instead of loadMore() so it sees the reset values
        isLoading = true
        val result = modrinth.search(
            facets = facets.build(),
            offset = 0,
            limit = limit,
            query = query
        )
        projects = result.hits
        offset = limit
        hasMore = limit < result.totalHits
        isLoading = false
    }

    LaunchedEffect(endReached) {
        if (endReached) loadMore()
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(projects, key = { it.slug }) { project ->
            ProjectCard(project = project, onClick = { navController.navigate("/project/${project.projectId}") })
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
        }
    }
}