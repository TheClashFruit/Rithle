package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Loader(
    val icon: String,
    val name: String,
    @SerialName("supported_project_types")
    val supportedProjectTypes: List<String>,
    @SerialName("supported_games")
    val supportedGames: List<String>,
    @SerialName("supported_fields")
    val supportedFields: List<String>,
    val metadata: Metadata
)

@Serializable
data class Metadata(
    val platform: Boolean? = null
)