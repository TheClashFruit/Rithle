package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.Serializable

@Serializable
data class RithleUser(
    val created: String,
    val donated: Boolean
)
