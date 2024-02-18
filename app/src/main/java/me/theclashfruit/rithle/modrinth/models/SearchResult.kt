package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class SearchResult(
    val slug: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerializedName("client_side") val clientSide: String,
    @SerializedName("server_side") val serverSide: String,
    @SerializedName("project_type") val projectType: String,
    val downloads: Int,
    @SerializedName("icon_url") val iconUrl: String? = null,
    val color: Int? = null,
    @SerializedName("thread_id") val threadId: String? = null,
    @SerializedName("monetization_status") val monetizationStatus: String? = null,
    @SerializedName("project_id") val projectId: String,
    val author: String,
    @SerializedName("display_categories") val displayCategories: List<String>? = null,
    val versions: List<String>,
    val follows: Int,
    @SerializedName("date_created") val dateCreated: String,
    @SerializedName("date_modified") val dateModified: String,
    @SerializedName("latest_version") val latestVersion: String? = null,
    val license: String,
    val gallery: List<String>? = null,
    @SerializedName("featured_gallery") val featuredGallery: String? = null
)
