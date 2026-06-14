package me.theclashfruit.rithle.services.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DownloadReason {
    @SerialName("standalone")
    Standalone,

    @SerialName("dependency")
    Dependency,

    @SerialName("modpack")
    Modpack,

    @SerialName("update")
    Update
}

@Serializable
data class DownloadMeta(
    val reason: DownloadReason,
    @SerialName("game_version")
    val gameVersion: String,
    val loader: String
)