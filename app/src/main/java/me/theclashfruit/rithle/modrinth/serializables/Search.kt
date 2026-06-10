package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Search<T>(
    val hits: List<T>,
    val limit: Int,
    val offset: Int,

    @SerialName("total_hits") val totalHits: Int
)

@Serializable
data class ProjectResult(
    @SerialName("project_id")   val projectId: String,
    @SerialName("project_type") val projectType: String,

    val slug: String,
    val title: String,
    val author: String,
    val description: String,
    val categories: List<String>? = null,

    @SerialName("client_side") val clientSide: String,
    @SerialName("server_side") val serverSide: String,

    val downloads: Int,

    @SerialName("icon_url")  val iconUrl: String? = null,
    val color: Int? = null,

    @SerialName("thread_id") val threadId: String? = null,

    @SerialName("monetization_status") val monetizationStatus: String? = null,

    @SerialName("date_created") val dateCreated: String,
    @SerialName("date_modified") val dateModified: String,
    @SerialName("display_categories") val displayCategories: List<String>? = null,
    @SerialName("featured_gallery") val featuredGallery: String? = null,

    val follows: Int,
    val gallery: List<String>? = null,

    @SerialName("latest_version") val latestVersion: String? = null,

    val license: String,
    val versions: List<String>
)