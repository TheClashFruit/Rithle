package me.theclashfruit.rithle.util.serializables

import kotlinx.serialization.Serializable

@Serializable
data class RithleVersions(
    val latest: Int,
    val versions: HashMap<String, RithleVersionsVersion>
)

@Serializable
data class RithleVersionsVersion(
    val name: String,
    val changelog: String,
    val release: String,
    val download: RithleVersionsVersionDownload
)

@Serializable
data class RithleVersionsVersionDownload(
    val direct: String,
    val github: String
)