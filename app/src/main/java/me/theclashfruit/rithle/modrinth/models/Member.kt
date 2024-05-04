package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("team_id") val teamId: String,
    val user: User,
    val role: String,
    @SerializedName("is_owner") val isOwner: Boolean,
    val permissions: Any?,
    @SerializedName("organization_permissions") val organizationPermissions: Any?,
    val accepted: Boolean,
    @SerializedName("payouts_split") val payoutsSplit: Float?,
    val ordering: Int
)
