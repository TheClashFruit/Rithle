package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationAction(
    val title: String,
    @SerialName("action_route") val actionRoute: List<String>
)
