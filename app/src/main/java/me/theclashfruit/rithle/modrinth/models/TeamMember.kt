package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class TeamMember(
    @SerializedName("team_id") val teamId: String,
    val user: User,
    val role: String,
    val permissions: Int,
    val accepted: Boolean,
    @SerializedName("payouts_split") val payoutsSplit: Float,
    val ordering: Int,
)
