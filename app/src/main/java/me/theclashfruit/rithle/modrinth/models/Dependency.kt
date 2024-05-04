package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class Dependency(
    @SerializedName("version_id") val versionId: String?,
    @SerializedName("project_id") val projectId: String?,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("dependency_type") val dependencyType: String
)
