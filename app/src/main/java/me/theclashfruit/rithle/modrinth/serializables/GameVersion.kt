package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameVersion(
    val version: String,
    @SerialName("version_type")
    val versionType: String,
    val date: String
)
