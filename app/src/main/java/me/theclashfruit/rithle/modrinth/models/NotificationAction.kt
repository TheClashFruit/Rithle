package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class NotificationAction(
    val title: String? = null,
    @SerializedName("action_route") val actionRoute: List<String>? = null
)
