package me.theclashfruit.rithle.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.GameVersion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameVersionFilterBottomSheet(
    show: Boolean,
    onDismiss: (selection: List<GameVersion>) -> Unit
) {
    val modrinth = Modrinth.getInstance()

    val sheetState = rememberModalBottomSheetState()

    if (show) {
        var allGameVersions by remember { mutableStateOf<List<GameVersion>>(listOf()) }
        var showSnapshots by remember { mutableStateOf(false) }
        val selectedVersions = remember { mutableStateListOf<GameVersion>() }

        val filteredVersions = remember(allGameVersions, showSnapshots) {
            allGameVersions.filter { showSnapshots || it.versionType == "release" }
        }

        LaunchedEffect(Unit) {
            allGameVersions = modrinth.gameVersions()
        }

        ModalBottomSheet(
            onDismissRequest = {
                onDismiss(selectedVersions.toList())
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Select Game Version",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showSnapshots = !showSnapshots }
                    ) {
                        Text(
                            text = "Snapshots",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Checkbox(
                            checked = showSnapshots,
                            onCheckedChange = { showSnapshots = it }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredVersions) { version ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedVersions.contains(version)) {
                                        selectedVersions.remove(version)
                                    } else {
                                        selectedVersions.add(version)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedVersions.contains(version),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedVersions.add(version)
                                    } else {
                                        selectedVersions.remove(version)
                                    }
                                }
                            )
                            Text(text = version.version, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterBottomSheetWithIcons(
    show: Boolean,
    title: String,
    items: List<T>,
    icon: (item: T) -> String,
    label: (item: T) -> String,
    onDismiss: (selection: List<T>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    if (show) {
        val selected = remember { mutableStateListOf<T>() }

        ModalBottomSheet(
            onDismissRequest = {
                onDismiss(selected.toList())
            },
            sheetState = sheetState
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selected.contains(item)) selected.remove(item)
                                else selected.add(item)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SvgStringIcon(icon(item), contentDescription = null, modifier = Modifier.size(24.dp))
                        Text(text = label(item), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        Checkbox(checked = selected.contains(item), onCheckedChange = null)
                    }
                }
            }
        }
    }
}