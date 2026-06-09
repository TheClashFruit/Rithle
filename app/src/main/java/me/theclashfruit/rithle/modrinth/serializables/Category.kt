package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val icon: String,
    val name: String,
    @SerialName("project_type")
    val projectType: String,
    val header: String
)
