package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class User(
    val username: String,
    val name: String? = null,
    val email: String? = null,
    val bio: String? = null,
    @SerializedName("payout_data") val payoutData: UserPayoutData? = null,
    val id: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val created: String,
    val role: String,
    val badges: Int? = null,
    @SerializedName("auth_providers") val authProviders: List<String>? = null,
    @SerializedName("email_verified") val emailVerified: String? = null,
    @SerializedName("has_password") val hasPassword: String? = null,
    @SerializedName("has_totp") val hasTotp: String? = null,
    @SerializedName("github_id") val githubId: String? = null,
)
