package me.theclashfruit.rithle.modrinth.models

data class NotificationBody(
    val type: String? = null,
    val notification_type: String? = null,
    val title: String? = null,
    val text: String? = null,
    val link: String? = null,
    val actions: List<NotificationAction>? = null,
    val project_id: String? = null,
    val organization_id: String? = null,
    val team_id: String? = null,
    val invited_by: String? = null,
    val role: String? = null,
    val old_status: String? = null,
    val new_status: String? = null,
    val version_id: String? = null,
)