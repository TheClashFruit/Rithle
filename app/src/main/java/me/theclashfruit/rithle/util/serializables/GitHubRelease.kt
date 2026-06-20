package me.theclashfruit.rithle.util.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    val url: String,
    @SerialName("assets_url")
    val assetsUrl: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val id: Long,
    val author: GitHubUser,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("target_commitish")
    val targetCommitish: String,
    val name: String,
    val draft: Boolean,
    val immutable: Boolean,
    val prerelease: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("published_at")
    val publishedAt: String,
    val assets: List<ReleaseAsset>,
    @SerialName("tarball_url")
    val tarballUrl: String,
    @SerialName("zipball_url")
    val zipballUrl: String,
    val body: String,
    val reactions: Reactions? = null
)

@Serializable
data class GitHubUser(
    val login: String,
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("gravatar_id")
    val gravatarId: String,
    val url: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("followers_url")
    val followersUrl: String,
    @SerialName("following_url")
    val followingUrl: String,
    @SerialName("gists_url")
    val gistsUrl: String,
    @SerialName("starred_url")
    val starredUrl: String,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String,
    @SerialName("organizations_url")
    val organizationsUrl: String,
    @SerialName("repos_url")
    val reposUrl: String,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("received_events_url")
    val receivedEventsUrl: String,
    val type: String,
    @SerialName("user_view_type")
    val userViewType: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean
)

@Serializable
data class ReleaseAsset(
    val url: String,
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    val name: String,
    val label: String? = null,
    val uploader: GitHubUser,
    @SerialName("content_type")
    val contentType: String,
    val state: String,
    val size: Long,
    val digest: String? = null,
    @SerialName("download_count")
    val downloadCount: Long,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String
)

@Serializable
data class Reactions(
    val url: String,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("+1")
    val plusOne: Int,
    @SerialName("-1")
    val minusOne: Int,
    val laugh: Int,
    val hooray: Int,
    val confused: Int,
    val heart: Int,
    val rocket: Int,
    val eyes: Int
)
