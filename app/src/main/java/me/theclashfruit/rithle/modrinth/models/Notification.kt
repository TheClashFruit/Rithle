package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val type: String? = null,
    val title: String? = null,
    val text: String? = null,
    val link: String? = null,
    val read: Boolean? = null,
    val created: String? = null,
    val body: NotificationBody? = null,
    val actions: List<NotificationAction>? = null,
)