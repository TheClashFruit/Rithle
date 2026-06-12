package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    @SerialName("user_id") val userIdd: String,
    val type: String? = null,
    val title: String,
    var text: String,
    val link: String,
    val read: Boolean,
    val created: String,
    val actions: List<NotificationAction>
)
