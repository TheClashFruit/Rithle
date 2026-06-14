package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectMember(
    @SerialName("team_id")
    val teamId: String,
    val user: User,
    val role: String,
    val permissions: Int? = null,
    @SerialName("organization_permissions")
    val organizationPermissions: Int? = null,
    val accepted: Boolean = false,
    @SerialName("payouts_split")
    val payoutsSplit: Float? = null,
    val ordering: Int
)
