package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class Version(
    val name: String,
    @SerializedName("version_number") val versionNumber: String,
    val changelog: String?,
    val dependencies: List<Dependency>,
    @SerializedName("game_versions") val gameVersions: List<String>,
    @SerializedName("version_type") val versionType: String,
    val loaders: List<String>,
    val featured: Boolean,
    val status: String?,
    @SerializedName("requested_status") val requestedStatus: String?,
    val id: String,
    @SerializedName("project_id") val projectId: String,
    @SerializedName("author_id") val authorId: String,
    @SerializedName("date_published") val datePublished: String,
    val downloads: Int,
    @Deprecated("Use `version.changelog`.") @SerializedName("changelog_url") val changelogUrl: String?,
    val files: List<File>
)
