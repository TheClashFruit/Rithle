package me.theclashfruit.rithle.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import me.theclashfruit.rithle.modrinth.serializables.Version

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadSelectionDialog(
    versions: List<Version>,
    onCancel: () -> Unit,
    onDownload: (selectedLoader: String, selectedGameVersion: String) -> Unit
) {
    val loaders = versions.flatMap { it.loaders }.toSet()
    val sortedLoaders = remember(loaders) { loaders.sorted() }

    var selectedLoader by remember { mutableStateOf(sortedLoaders.firstOrNull() ?: "") }

    val availableGameVersions = remember(selectedLoader, versions) {
        versions
            .filter { it.loaders.contains(selectedLoader) }
            .flatMap { it.gameVersions }
            .distinct()
    }
    val sortedGameVersions = remember(availableGameVersions) { availableGameVersions }

    var selectedGameVersion by remember { mutableStateOf(sortedGameVersions.firstOrNull() ?: "") }

    var loaderExpanded by remember { mutableStateOf(false) }
    var gameVersionExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(
                text = "Download"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = loaderExpanded,
                    onExpandedChange = { loaderExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLoader.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loader") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loaderExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = loaderExpanded,
                        onDismissRequest = { loaderExpanded = false }
                    ) {
                        sortedLoaders.forEach { loader ->
                            DropdownMenuItem(
                                text = { Text(loader.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedLoader = loader

                                    val newAvailable =
                                        versions.filter { it.loaders.contains(loader) }
                                            .flatMap { it.gameVersions }
                                    if (!newAvailable.contains(selectedGameVersion)) {
                                        selectedGameVersion = newAvailable.firstOrNull() ?: ""
                                    }
                                    loaderExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = gameVersionExpanded,
                    onExpandedChange = { gameVersionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGameVersion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Game Version") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gameVersionExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = gameVersionExpanded,
                        onDismissRequest = { gameVersionExpanded = false }
                    ) {
                        sortedGameVersions.forEach { version ->
                            DropdownMenuItem(
                                text = { Text(version) },
                                onClick = {
                                    selectedGameVersion = version
                                    gameVersionExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDownload(selectedLoader, selectedGameVersion)
                },
                enabled = selectedLoader.isNotEmpty() && selectedGameVersion.isNotEmpty()
            ) {
                Text("Download")
            }
        },
        onDismissRequest = onCancel,
    )
}