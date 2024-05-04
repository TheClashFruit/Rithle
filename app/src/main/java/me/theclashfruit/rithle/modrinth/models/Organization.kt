package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class Organization(
    val id: String,
    val slug: String,
    val name: String,
    @SerializedName("team_id") val teamId: String,
    val description: String,
    @SerializedName("icon_url") val iconUrl: String,
    val color: Int,
    val members: List<Member>
)
