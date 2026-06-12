package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Version(
    @SerialName("name") val name: String,
    @SerialName("version_number") val versionNumber: String,
    @SerialName("changelog") val changelog: String?,
    @SerialName("dependencies") val dependencies: List<Dependency>,
    @SerialName("game_versions") val gameVersions: List<String>,
    @SerialName("version_type") val versionType: String,
    @SerialName("loaders") val loaders: List<String>,
    @SerialName("featured") val featured: Boolean,
    @SerialName("status") val status: String,
    @SerialName("requested_status") val requestedStatus: String?,
    @SerialName("id") val id: String,
    @SerialName("project_id") val projectId: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("date_published") val datePublished: String,
    @SerialName("downloads") val downloads: Int,
    @Deprecated("Always null, only kept for legacy compatibility.")
    @SerialName("changelog_url") val changelogUrl: String? = null,
    @SerialName("files") val files: List<File>
)

@Serializable
data class Dependency(
    @SerialName("version_id") val versionId: String?,
    @SerialName("project_id") val projectId: String?,
    @SerialName("file_name") val fileName: String?,
    @SerialName("dependency_type") val dependencyType: String
)

@Serializable
data class File(
    @SerialName("hashes") val hashes: Map<String, String>,
    @SerialName("url") val url: String,
    @SerialName("filename") val filename: String,
    @SerialName("primary") val primary: Boolean,
    @SerialName("size") val size: Int,
    @SerialName("file_type") val fileType: String?
)