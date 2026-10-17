package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.Serializable

@Serializable
data class RithleUser(
    val id: String,
    val modrinthId: String,
    val supporter: Boolean,
    val createdAt: String
)
