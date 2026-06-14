package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.theclashfruit.rithle.modrinth.enums.MonetizationStatus
import me.theclashfruit.rithle.modrinth.enums.Side

import me.theclashfruit.rithle.modrinth.enums.Status

@Serializable
data class Project(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    val team: String,
    val body: String,
    val status: Status,
    val downloads: Int,
    val followers: Int,
    val published: String,
    val updated: String,

    val color: Int? = null,
    val approved: String? = null,
    val queued: String? = null,
    val license: License? = null,
    val versions: List<String>? = null,
    val gallery: List<Gallery>? = null,
    val loaders: List<String>? = null,
    val organization: String? = null,

    @SerialName("client_side") val clientSide: Side,
    @SerialName("server_side") val serverSide: Side,
    @SerialName("additional_categories") val additionalCategories: List<String>,
    @SerialName("project_type") val projectType: String,
    @SerialName("thread_id") val threadId: String,
    @SerialName("monetization_status") val monetizationStatus: MonetizationStatus,

    @SerialName("issues_url") val issuesUrl: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("wiki_url") val wikiUrl: String? = null,
    @SerialName("discord_url") val discordUrl: String? = null,
    @SerialName("donation_urls") val donationUrls: List<DonationUrl>? = null,
    @SerialName("game_versions") val gameVersions: List<String>? = null,
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("requested_status") val requestedStatus: Status? = null,

    @Deprecated("Always null, only kept for legacy compatibility.")
    @SerialName("body_url") val bodyUrl: String? = null,

    @Deprecated("Use `threadId` instead.")
    @SerialName("moderator_message") val moderatorMessage: ModeratorMessage? = null
)

@Serializable
data class License(
    val id: String,
    val name: String,
    val url: String? = null
)

@Serializable
data class Gallery(
    val created: String,
    val description: String? = null,
    val featured: Boolean,
    val ordering: Long,
    val title: String? = null,
    val url: String
)

@Serializable
data class ModeratorMessage(
    val message: String,
    val body: String? = null,
)