package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String?,
    val bio: String?,
    @SerialName("payout_data")
    val payoutData: PayoutData? = null,
    @SerialName("avatar_url")
    val avatarUrl: String,
    val created: String,
    val role: String,
    val badges: Int? = null,
    val campaigns: Campaigns,
    @SerialName("auth_providers")
    val authProviders: List<String>? = null,
    @SerialName("email_verified")
    val emailVerified: Boolean? = null,
    @SerialName("has_password")
    val hasPassword: Boolean? = null,
    @SerialName("has_totp")
    val hasTotp: Boolean? = null,
    @SerialName("github_id")
    @Deprecated("This is no longer public for security reasons and is always null.")
    val githubId: Int? = null
)

@Serializable
data class Campaigns(
    @SerialName("pride_26")
    val pride26: Pride26
)

@Serializable
data class Pride26(
    @SerialName("last_donated_at")
    val lastDonatedAt: String,
    @SerialName("has_badge")
    val hasBadge: Boolean,
    @SerialName("has_midas")
    val hasMidas: Boolean,
)

@Serializable
data class PayoutData(
    val balance: Double? = null,
    @SerialName("payout_wallet")
    val payoutWallet: String? = null,
    @SerialName("payout_wallet_type")
    val payoutWalletType: String? = null,
    @SerialName("payout_address")
    val payoutAddress: String? = null
)
